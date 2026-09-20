package domain.model.sync

import domain.model.BatchId
import domain.model.CategoryId
import domain.model.LocationId
import domain.model.ProductId

data class SyncPayload (
    val createdCategories: List<SyncCategory>,
    val updatedCategories: List<SyncCategory>,
    val deletedCategories: List<CategoryId>,

    val createdLocations: List<SyncLocation>,
    val updatedLocations: List<SyncLocation>,
    val deletedLocations: List<LocationId>,

    val createdProduct: List<SyncProduct>,
    val updatedProduct: List<SyncProduct>,
    val deletedProduct: List<ProductId>,

    val createdBatches: List<SyncStockBatch>,
    val updatedBatches: List<SyncStockBatch>,
    val deletedBatches: List<BatchId>
)