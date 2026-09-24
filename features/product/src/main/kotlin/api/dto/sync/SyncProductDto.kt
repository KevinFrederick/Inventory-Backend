package api.dto.sync

import kotlinx.serialization.Serializable

@Serializable
data class SyncProductDto(
    val productId: String,
    val categoryId: String,
    val name: String,
    val description: String? = null,
    val barcode: String? = null,
    val barcodeFormat: String? = null,
    val sku: String? = null,
    val imageUri: String? = null,
    val minimumQuantity: Int = 0,
    val createdAt: Long,
    val lastUpdated: Long
)
