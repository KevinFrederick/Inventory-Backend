package domain.repository

import domain.model.BatchId
import domain.model.StockBatch
import result.DomainResult

interface StockBatchRepository {
    suspend fun getBatchById(batchId: BatchId): DomainResult<StockBatch?>
    suspend fun insertBatch(batch: StockBatch): DomainResult<StockBatch>
    suspend fun updateBatch(batch: StockBatch): DomainResult<StockBatch>
    suspend fun deleteBatch(batchId: BatchId, deletedTimestamp: Long): DomainResult<Unit>
}