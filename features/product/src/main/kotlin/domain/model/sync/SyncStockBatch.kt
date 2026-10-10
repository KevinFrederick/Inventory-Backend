package domain.model.sync

import domain.model.BatchId
import domain.model.LocationId
import domain.model.ProductId
import model.GroupId

data class SyncStockBatch(
    val batchId: BatchId,
    val productId: ProductId,
    val locationId: LocationId,
    val groupId: GroupId,
    val quantity: Int,
    val price: Double,
    val expirationDate: Long?,
    val supplier: String?,
    val createdAt: Long,
    val lastUpdated: Long
)
