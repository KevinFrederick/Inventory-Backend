package domain.model

data class ProductParams(
    val productId: ProductId,
    val categoryId: CategoryId,
    val name: String,
    val description: String?,
    val barcode: String?,
    val barcodeFormat: String?,
    val sku: String?,
    val imageUri: String?,
    val minimumQuantity: Int,
    val batches: List<StockBatchParams>,
    val createdAt: Long,
    val lastUpdated: Long
)
