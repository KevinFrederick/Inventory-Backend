package domain.model.sync

import domain.model.CategoryId
import domain.model.ProductId

data class SyncProduct (
    val productId: ProductId,
    val categoryId: CategoryId,
    val name: String,
    val description: String? = null,
    val barcode: String? = null,
    val sku: String? = null,
    val imageUri: String? = null,
    val minimumQuantity: Int = 0,
    val createdAt: Long,
    val lastUpdated: Long
)