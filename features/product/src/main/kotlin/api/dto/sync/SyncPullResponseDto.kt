package api.dto.sync

import kotlinx.serialization.Serializable

@Serializable
data class SyncPullResponseDto(
    val categories: List<SyncCategoryDto>,
    val locations: List<SyncLocationDto>,
    val products: List<SyncProductDto>,
    val batches: List<SyncStockBatchDto>,

    val deletedCategories: List<String>,
    val deletedLocations: List<String>,
    val deletedProducts: List<String>,
    val deletedBatches: List<String>,

    val serverTimeStamp: Long,
)
