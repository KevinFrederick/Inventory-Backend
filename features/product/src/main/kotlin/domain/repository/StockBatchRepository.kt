package domain.repository

import domain.model.BatchId
import domain.model.StockBatch

interface StockBatchRepository {
    suspend fun getBatchById(batchId: BatchId): StockBatch?
    suspend fun insertBatch(batch: StockBatch): Boolean
    suspend fun updateBatch(batch: StockBatch): Boolean
    suspend fun deleteBatch(batchId: BatchId, deletedTimestamp: Long): Boolean
}