package domain.usecase.stockbatch

import domain.model.BatchId
import domain.model.StockBatch
import domain.repository.StockBatchRepository
import domain.result.DomainResult
import domain.result.ErrorType

class UpdateStockBatchUseCase(
    private val repository: StockBatchRepository
) {
    suspend operator fun invoke(
        batchId: BatchId,
        batch: StockBatch
    ): DomainResult<Unit> {
        return if (batch.batchId != batchId) {
            DomainResult.Error("Batch id doesn't match", ErrorType.BAD_REQUEST)
        } else {
            repository.updateBatch(batch)
        }
    }
}