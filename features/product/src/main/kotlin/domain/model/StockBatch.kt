package domain.model

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class BatchId(val value: String)

@Serializable
data class StockBatch(
    val batchId: BatchId,
    val productId: ProductId,
    val location: Location,
    val quantity: Int,
    val price: Double,
    val expirationDate: Long?,
    val supplier: String?,
    val lastUpdated: Long
)
