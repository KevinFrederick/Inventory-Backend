package domain.model.sync

import domain.model.CategoryId
import domain.model.ProductId

data class SyncProduct (
    val productId: ProductId,
    val categoryId: CategoryId,
    val name: String,
    val description: String?,
    val barcode: String?,
    val sku: String?,
    val imageUri: String?,
    val minimumQuantity: Int,
    val createdAt: Long,
    val lastUpdated: Long
)