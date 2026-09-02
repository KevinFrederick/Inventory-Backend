package data.repository

import DatabaseFactory.dbQuery
import data.local.table.LocationTable
import data.local.table.ProductTable
import data.local.table.StockBatchTable
import data.mapper.toStockBatch
import domain.model.BatchId
import domain.model.StockBatch
import domain.repository.StockBatchRepository
import domain.result.DomainResult
import domain.result.ErrorType
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

class StockBatchRepositoryImpl: StockBatchRepository {
    override suspend fun getBatchById(batchId: BatchId): DomainResult<StockBatch?> = dbQuery {
        try {
            val batchRow = StockBatchTable
                .innerJoin(LocationTable)
                .selectAll()
                .where { StockBatchTable.batchId eq batchId.value }
                .singleOrNull()

            if (batchRow == null) return@dbQuery DomainResult.Success(null)

            val batch = batchRow.toStockBatch()

            DomainResult.Success(batch)
        } catch (e: Exception) {
            DomainResult.Error(e.message ?: "Unknown error", ErrorType.UNKNOWN)
        }
    }

    override suspend fun insertBatch(batch: StockBatch): DomainResult<Unit> = dbQuery {
        try {
            val insertStatement = StockBatchTable.insert {
                it[batchId] = batch.batchId.value
                it[productId] = batch.productId.value
                it[locationId] = batch.location.locationId.value
                it[quantity] = batch.quantity
                it[price] = batch.price
                it[expirationDate] = batch.expirationDate
                it[supplier] = batch.supplier
                it[lastUpdated] = batch.lastUpdated
            }

            val isBatchInserted = insertStatement.insertedCount > 0

            if (isBatchInserted) {
                ProductTable.update({ ProductTable.productId eq batch.productId.value}) {
                    it[lastUpdated] = batch.lastUpdated
                }
            }

            DomainResult.Success(Unit)
        } catch (e: ExposedSQLException) {
            val errorMessage = e.message ?: ""

            when{
                errorMessage.contains("stock_batch_product_id_fkey") ->
                    DomainResult.Error("Invalid Product ID", ErrorType.NOT_FOUND)
                errorMessage.contains("stock_batch_location_id_fkey") ->
                    DomainResult.Error("Invalid Location ID", ErrorType.NOT_FOUND)
                else ->
                    DomainResult.Error("Failed to saved Batch", ErrorType.UNKNOWN)
            }
        }
    }

    override suspend fun updateBatch(batch: StockBatch): DomainResult<Unit> = dbQuery {
        try {
            val updatedRowsCount = StockBatchTable.update ({ StockBatchTable.batchId eq batch.batchId.value}) {
                it[locationId] = batch.location.locationId.value
                it[quantity] = batch.quantity
                it[price] = batch.price
                it[expirationDate] = batch.expirationDate
                it[supplier] = batch.supplier
                it[lastUpdated] = batch.lastUpdated
            }

            if (updatedRowsCount == 0) return@dbQuery DomainResult.Error("Batch not found", ErrorType.NOT_FOUND)

            ProductTable.update({ ProductTable.productId eq batch.productId.value}) {
                it[lastUpdated] = batch.lastUpdated
            }

            DomainResult.Success(Unit)
        } catch (e: ExposedSQLException) {
            val errorMessage = e.message ?: ""

            when{
                errorMessage.contains("stock_batch_product_id_fkey") ->
                    DomainResult.Error("Invalid Product ID", ErrorType.NOT_FOUND)
                errorMessage.contains("stock_batch_location_id_fkey") ->
                    DomainResult.Error("Invalid Location ID", ErrorType.NOT_FOUND)
                else ->
                    DomainResult.Error("Failed to saved Batch", ErrorType.UNKNOWN)
            }
        }
    }

    override suspend fun deleteBatch(batchId: BatchId, deletedTimestamp: Long): DomainResult<Unit> = dbQuery {
        try {
            val productId = StockBatchTable
                .selectAll()
                .where { StockBatchTable.batchId eq batchId.value }
                .singleOrNull()
                ?.get(StockBatchTable.productId)

            if (productId == null) return@dbQuery DomainResult.Error("Batch not found", ErrorType.NOT_FOUND)

            val deletedRowsCount = StockBatchTable.deleteWhere { StockBatchTable.batchId eq batchId.value }

            val isBatchDeleted = deletedRowsCount > 0

            if (isBatchDeleted) {
                ProductTable.update({ ProductTable.productId eq productId}) {
                    it[lastUpdated] = deletedTimestamp
                }
            }

            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Error(e.message ?: "Unknown error", ErrorType.UNKNOWN)
        }
    }

}