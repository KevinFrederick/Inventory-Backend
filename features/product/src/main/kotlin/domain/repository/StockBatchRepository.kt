package domain.repository

import domain.model.BatchId
import domain.model.StockBatch
import model.GroupId
import result.DomainResult

interface StockBatchRepository {
    suspend fun getBatchById(batchId: BatchId, groupId: GroupId): DomainResult<StockBatch>
    suspend fun insertBatch(batch: StockBatch, groupId: GroupId): DomainResult<StockBatch>
    suspend fun updateBatch(batch: StockBatch, groupId: GroupId): DomainResult<StockBatch>
    suspend fun deleteBatch(batchId: BatchId, deletedTimestamp: Long, groupId: GroupId): DomainResult<Unit>
}