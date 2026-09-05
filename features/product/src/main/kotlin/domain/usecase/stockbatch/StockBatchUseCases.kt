package domain.usecase.stockbatch

data class StockBatchUseCases(
    val getBatchById: GetStockBatchByIdUseCase,
    val insertBatch: InsertStockBatchUseCase,
    val updateBatch: UpdateStockBatchUseCase,
    val deleteBatch: DeleteStockBatchUseCase
)
