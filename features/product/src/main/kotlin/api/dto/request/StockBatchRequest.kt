package api.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class StockBatchRequest(
    val batchId: String,
    val productId: String,
    val locationId: String,
    val quantity: Int,
    val price: Double,
    val expirationDate: Long? = null,
    val supplier: String? = null,
    val createdAt: Long,
    val lastUpdated: Long
)
