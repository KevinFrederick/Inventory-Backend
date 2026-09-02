package domain.usecase.stockbatch

import domain.model.BatchId
import domain.model.StockBatch
import domain.repository.StockBatchRepository
import domain.result.DomainResult
import domain.result.ErrorType

class GetStockBatchByIdUseCase (
    private val repository: StockBatchRepository
) {
    suspend operator fun invoke(batchId: BatchId): DomainResult<StockBatch> {
        return when(
            val result = repository.getBatchById(batchId)
        ) {
            is DomainResult.Success -> {
                if (result.data == null) {
                    DomainResult.Error("Batch not found", ErrorType.NOT_FOUND)
                } else {
                    DomainResult.Success(result.data)
                }
            }
            is DomainResult.Error -> result
        }
    }
}