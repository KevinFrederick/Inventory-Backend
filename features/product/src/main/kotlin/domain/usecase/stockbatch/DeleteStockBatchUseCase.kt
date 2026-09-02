package domain.usecase.stockbatch

import domain.model.BatchId
import domain.repository.StockBatchRepository
import domain.result.DomainResult

class DeleteStockBatchUseCase(
    private val repository: StockBatchRepository
) {
    suspend operator fun invoke(batchId: BatchId): DomainResult<Unit> =
        repository.deleteBatch(
            batchId = batchId,
            deletedTimestamp = System.currentTimeMillis()
        )
}