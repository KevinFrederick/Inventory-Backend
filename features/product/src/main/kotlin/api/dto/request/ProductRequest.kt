package api.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ProductRequest(
    val productId: String,
    val categoryId: String,
    val name: String,
    val description: String? = null,
    val barcode: String? = null,
    val barcodeFormat: String? = null,
    val sku: String? = null,
    val imageUri: String? = null,
    val minimumQuantity: Int = 0,
    val batches: List<StockBatchRequest> = emptyList(),
    val createdAt: Long,
    val lastUpdated: Long
)
