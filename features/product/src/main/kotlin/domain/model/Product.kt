package domain.model

import model.GroupId

@JvmInline
value class ProductId(val value: String)

data class Product(
    val productId: ProductId,
    val groupId: GroupId,
    val category: Category,
    val name: String,
    val description: String?,
    val barcode: String?,
    val barcodeFormat: String?,
    val sku: String?,
    val imageUri: String?,
    val minimumQuantity: Int,
    val batches: List<StockBatch>,
    val createdAt: Long,
    val lastUpdated: Long
)
