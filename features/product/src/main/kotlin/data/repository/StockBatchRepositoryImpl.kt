package data.repository

import DatabaseFactory.dbQuery
import data.table.product.DeletedTable
import data.table.product.LocationTable
import data.table.product.ProductTable
import data.table.product.StockBatchTable
import data.mapper.toStockBatch
import data.util.EntityType
import domain.model.BatchId
import domain.model.StockBatch
import domain.repository.StockBatchRepository
import model.GroupId
import org.jetbrains.exposed.v1.core.and
import result.DomainResult
import result.ErrorType
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import org.slf4j.LoggerFactory

class StockBatchRepositoryImpl: StockBatchRepository {
    private val logger = LoggerFactory.getLogger(StockBatchRepositoryImpl::class.java)

    override suspend fun getBatchById(batchId: BatchId, groupId: GroupId): DomainResult<StockBatch> = dbQuery {
        try {
            val batchRow = StockBatchTable
                .innerJoin(LocationTable)
                .selectAll()
                .where { (StockBatchTable.batchId eq batchId.value) and (StockBatchTable.groupId eq groupId.value) }
                .singleOrNull()

            if (batchRow == null) return@dbQuery DomainResult.Error("Batch not found", ErrorType.NOT_FOUND)

            val batch = batchRow.toStockBatch()

            DomainResult.Success(batch)
        } catch (e: Exception) {
            logger.error("Error fetching stock batch", e)
            DomainResult.Error(e.message ?: "Unknown error", ErrorType.UNKNOWN)
        }
    }

    override suspend fun insertBatch(batch: StockBatch, groupId: GroupId): DomainResult<StockBatch> = dbQuery {
        try {
            val timeStamp = System.currentTimeMillis()

            val insertStatement = StockBatchTable.insert {
                it[batchId] = batch.batchId.value
                it[productId] = batch.productId.value
                it[locationId] = batch.location.locationId.value
                it[this.groupId] = groupId.value
                it[quantity] = batch.quantity
                it[price] = batch.price
                it[expirationDate] = batch.expirationDate
                it[supplier] = batch.supplier
                it[createdAt] = batch.createdAt
                it[lastUpdated] = batch.lastUpdated
                it[serverUpdatedAt] = timeStamp
            }

            val isBatchInserted = insertStatement.insertedCount > 0

            if (isBatchInserted) {
                ProductTable.update({
                    (ProductTable.productId eq batch.productId.value) and (ProductTable.groupId eq groupId.value)
                }) {
                    it[lastUpdated] = batch.lastUpdated
                    it[serverUpdatedAt] = timeStamp
                }
            }

            DomainResult.Success(batch)
        } catch (e: ExposedSQLException) {
            logger.error("Error inserting batch", e)
            val errorMessage = e.message ?: ""

            when{
                errorMessage.contains("stock_batch_product_id_fkey") ->
                    DomainResult.Error("Product Not Found", ErrorType.NOT_FOUND)
                errorMessage.contains("stock_batch_location_id_fkey") ->
                    DomainResult.Error("Location Not Found", ErrorType.NOT_FOUND)
                else ->
                    DomainResult.Error("Failed to saved Batch", ErrorType.UNKNOWN)
            }
        }
    }

    override suspend fun updateBatch(batch: StockBatch, groupId: GroupId): DomainResult<StockBatch> = dbQuery {
        try {
            val timeStamp = System.currentTimeMillis()

            val updatedRowsCount = StockBatchTable.update ({
                (StockBatchTable.batchId eq batch.batchId.value) and (StockBatchTable.groupId eq groupId.value)
            }) {
                it[locationId] = batch.location.locationId.value
                it[quantity] = batch.quantity
                it[price] = batch.price
                it[expirationDate] = batch.expirationDate
                it[supplier] = batch.supplier
                it[lastUpdated] = batch.lastUpdated
                it[serverUpdatedAt] = timeStamp
            }

            if (updatedRowsCount == 0) return@dbQuery DomainResult.Error("Batch not found", ErrorType.NOT_FOUND)

            ProductTable.update({
                (ProductTable.productId eq batch.productId.value) and (ProductTable.groupId eq groupId.value)
            }) {
                it[lastUpdated] = batch.lastUpdated
                it[serverUpdatedAt] = timeStamp
            }

            DomainResult.Success(batch)
        } catch (e: ExposedSQLException) {
            logger.error("Error updating batch", e)
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

    override suspend fun deleteBatch(batchId: BatchId, deletedTimestamp: Long, groupId: GroupId): DomainResult<Unit> = dbQuery {
        try {
            val timeStamp = System.currentTimeMillis()

            val productId = StockBatchTable
                .select(StockBatchTable.productId)
                .where { (StockBatchTable.batchId eq batchId.value) and (StockBatchTable.groupId eq groupId.value) }
                .singleOrNull()
                ?.get(StockBatchTable.productId)

            if (productId == null) return@dbQuery DomainResult.Error("Batch not found", ErrorType.NOT_FOUND)

            val deletedRowsCount = StockBatchTable.deleteWhere {
                (StockBatchTable.batchId eq batchId.value) and (StockBatchTable.groupId eq groupId.value)
            }

            val isBatchDeleted = deletedRowsCount > 0

            if (isBatchDeleted) {
                ProductTable.update({
                    (ProductTable.productId eq productId) and (ProductTable.groupId eq groupId.value)
                }) {
                    it[lastUpdated] = deletedTimestamp
                    it[serverUpdatedAt] = timeStamp
                }

                DeletedTable.insert {
                    it[entityId] = batchId.value
                    it[this.groupId] = groupId.value
                    it[entityType] = EntityType.BATCH.name
                    it[deletedAt] = timeStamp
                }
            }

            DomainResult.Success(Unit)
        } catch (e: Exception) {
            logger.error("Error deleting batch", e)
            DomainResult.Error(e.message ?: "Unknown error", ErrorType.UNKNOWN)
        }
    }
}