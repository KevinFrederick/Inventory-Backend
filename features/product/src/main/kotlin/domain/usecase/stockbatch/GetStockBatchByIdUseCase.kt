package domain.usecase.stockbatch

import domain.model.BatchId
import domain.model.StockBatch
import domain.repository.StockBatchRepository
import result.DomainResult
import result.ErrorType

class GetStockBatchByIdUseCase (
    private val repository: StockBatchRepository
) {
    suspend operator fun invoke(batchId: BatchId): DomainResult<StockBatch> {
        return when(
            val result = repository.getBatchById(batchId)
        ) {
            is DomainResult.Success -> {
                val batch = result.data

                if (batch == null) {
                    DomainResult.Error("Batch not found", ErrorType.NOT_FOUND)
                } else {
                    DomainResult.Success(batch)
                }
            }
            is DomainResult.Error -> result
        }
    }
}