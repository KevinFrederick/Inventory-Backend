package api.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class StockBatchResponse(
    val batchId: String,
    val groupId: String,
    val productId: String,
    val location: LocationResponse,
    val quantity: Int,
    val price: Double,
    val expirationDate: Long? = null,
    val supplier: String? = null,
    val createdAt: Long,
    val lastUpdated: Long
)
