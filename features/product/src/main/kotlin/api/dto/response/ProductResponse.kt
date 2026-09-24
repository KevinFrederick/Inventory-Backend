package api.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse (
    val productId: String,
    val category: CategoryResponse,
    val name: String,
    val description: String?,
    val barcode: String?,
    val barcodeFormat: String?,
    val sku: String?,
    val imageUri: String?,
    val minimumQuantity: Int,
    val batches: List<StockBatchResponse>,
    val createdAt: Long,
    val lastUpdated: Long
)