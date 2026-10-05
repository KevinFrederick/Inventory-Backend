package domain.usecase.stockbatch

import domain.model.BatchId
import model.GroupId
import domain.model.StockBatch
import domain.repository.StockBatchRepository
import result.DomainResult

class GetStockBatchByIdUseCase (
    private val repository: StockBatchRepository
) {
    suspend operator fun invoke(
        batchId: BatchId,
        groupId: GroupId,
    ): DomainResult<StockBatch> =
        repository.getBatchById(batchId, groupId)
}