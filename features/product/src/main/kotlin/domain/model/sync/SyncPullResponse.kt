package domain.model.sync

import domain.model.BatchId
import domain.model.Category
import domain.model.CategoryId
import domain.model.Location
import domain.model.LocationId
import domain.model.ProductId

data class SyncPullResponse(
    val categories: List<Category>,
    val locations: List<Location>,
    val products: List<SyncProduct>,
    val batches: List<SyncStockBatch>,

    val deletedCategories: List<CategoryId>,
    val deletedLocations: List<LocationId>,
    val deletedProducts: List<ProductId>,
    val deletedBatches: List<BatchId>,

    val serverTimeStamp: Long,
)
