package data.repository

import DatabaseFactory.dbQuery
import data.local.table.ProductTable
import data.local.table.StockBatchTable
import data.mapper.toStockBatch
import domain.model.BatchId
import domain.model.StockBatch
import domain.repository.StockBatchRepository
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

class StockBatchRepositoryImpl: StockBatchRepository {
    override suspend fun getBatchById(batchId: BatchId): StockBatch? = dbQuery {
        val batchRow = StockBatchTable
            .selectAll()
            .where { StockBatchTable.batchId eq batchId.value }
            .singleOrNull()

        if (batchRow == null) return@dbQuery null

        batchRow.toStockBatch()
    }

    override suspend fun insertBatch(batch: StockBatch): Boolean = dbQuery {
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

        return@dbQuery isBatchInserted
    }

    override suspend fun updateBatch(batch: StockBatch): Boolean = dbQuery {
        val updatedRowsCount = StockBatchTable.update ({ StockBatchTable.batchId eq batch.batchId.value}) {
            it[locationId] = batch.location.locationId.value
            it[quantity] = batch.quantity
            it[price] = batch.price
            it[expirationDate] = batch.expirationDate
            it[supplier] = batch.supplier
            it[lastUpdated] = batch.lastUpdated
        }

        val isBatchUpdated = updatedRowsCount > 0

        if (isBatchUpdated) {
            ProductTable.update({ ProductTable.productId eq batch.productId.value}) {
                it[lastUpdated] = batch.lastUpdated
            }
        }

        return@dbQuery isBatchUpdated
    }

    override suspend fun deleteBatch(batchId: BatchId, deletedTimestamp: Long): Boolean = dbQuery {
        val productId = StockBatchTable
            .selectAll()
            .where { StockBatchTable.batchId eq batchId.value }
            .singleOrNull()
            ?.get(StockBatchTable.productId)

        if (productId == null) return@dbQuery false

        val deletedRowsCount = StockBatchTable.deleteWhere { StockBatchTable.batchId eq batchId.value }

        val isBatchDeleted = deletedRowsCount > 0

        if (isBatchDeleted) {
            ProductTable.update({ ProductTable.productId eq productId}) {
                it[lastUpdated] = deletedTimestamp
            }
        }

        return@dbQuery deletedRowsCount > 0
    }

}