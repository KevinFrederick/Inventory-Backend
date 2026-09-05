package domain.model

data class StockBatchParams(
    val batchId: BatchId,
    val productId: ProductId,
    val locationId: LocationId,
    val quantity: Int,
    val price: Double,
    val expirationDate: Long?,
    val supplier: String?,
    val createdAt: Long,
    val lastUpdated: Long
)
