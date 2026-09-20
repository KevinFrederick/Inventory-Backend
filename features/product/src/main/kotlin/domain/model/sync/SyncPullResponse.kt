package domain.model.sync

import domain.model.BatchId
import domain.model.CategoryId
import domain.model.LocationId
import domain.model.ProductId

data class SyncPullResponse(
    val categories: List<SyncCategory>,
    val locations: List<SyncLocation>,
    val products: List<SyncProduct>,
    val batches: List<SyncStockBatch>,

    val deletedCategories: List<CategoryId>,
    val deletedLocations: List<LocationId>,
    val deletedProducts: List<ProductId>,
    val deletedBatches: List<BatchId>,

    val serverTimeStamp: Long,
)
