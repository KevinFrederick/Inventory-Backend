package domain.model

import model.GroupId

@JvmInline
value class BatchId(val value: String)

data class StockBatch(
    val batchId: BatchId,
    val productId: ProductId,
    val groupId: GroupId,
    val location: Location,
    val quantity: Int,
    val price: Double,
    val expirationDate: Long?,
    val supplier: String?,
    val createdAt: Long,
    val lastUpdated: Long
)
