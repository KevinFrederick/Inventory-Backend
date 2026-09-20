package domain.model.sync

import domain.model.BatchId
import domain.model.LocationId
import domain.model.ProductId

data class SyncStockBatch(
    val batchId: BatchId,
    val productId: ProductId,
    val locationId: LocationId,
    val quantity: Int,
    val price: Double,
    val expirationDate: Long? = null,
    val supplier: String? = null,
    val createdAt: Long,
    val lastUpdated: Long
)
