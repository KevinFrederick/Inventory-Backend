package domain.usecase.stockbatch

import domain.model.StockBatch
import domain.repository.StockBatchRepository
import domain.result.DomainResult

class InsertStockBatchUseCase (
    private val repository: StockBatchRepository
) {
    suspend operator fun invoke(batch: StockBatch): DomainResult<Unit> =
        repository.insertBatch(batch)
}