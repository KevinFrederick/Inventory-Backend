package api.dto.sync

import kotlinx.serialization.Serializable

@Serializable
data class SyncStockBatchDto(
    val batchId: String,
    val productId: String,
    val locationId: String,
    val groupId: String,
    val quantity: Int,
    val price: Double,
    val expirationDate: Long? = null,
    val supplier: String? = null,
    val createdAt: Long,
    val lastUpdated: Long
)
