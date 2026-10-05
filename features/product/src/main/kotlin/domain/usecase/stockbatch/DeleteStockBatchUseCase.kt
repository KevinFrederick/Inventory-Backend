package domain.usecase.stockbatch

import domain.model.BatchId
import model.GroupId
import domain.repository.StockBatchRepository
import result.DomainResult

class DeleteStockBatchUseCase(
    private val repository: StockBatchRepository
) {
    suspend operator fun invoke(
        batchId: BatchId,
        groupId: GroupId,
    ): DomainResult<Unit> =
        repository.deleteBatch(
            batchId = batchId,
            deletedTimestamp = System.currentTimeMillis(),
            groupId = groupId,
        )
}