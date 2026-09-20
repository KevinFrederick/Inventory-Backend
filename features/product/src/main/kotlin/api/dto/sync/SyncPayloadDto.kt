package api.dto.sync

import kotlinx.serialization.Serializable

@Serializable
data class SyncPayloadDto(
    val createdCategories: List<SyncCategoryDto> = emptyList(),
    val updatedCategories: List<SyncCategoryDto> = emptyList(),
    val deletedCategories: List<String> = emptyList(),

    val createdLocations: List<SyncLocationDto> = emptyList(),
    val updatedLocations: List<SyncLocationDto> = emptyList(),
    val deletedLocations: List<String> = emptyList(),

    val createdProduct: List<SyncProductDto> = emptyList(),
    val updatedProduct: List<SyncProductDto> = emptyList(),
    val deletedProduct: List<String> = emptyList(),

    val createdBatches: List<SyncStockBatchDto> = emptyList(),
    val updatedBatches: List<SyncStockBatchDto> = emptyList(),
    val deletedBatches: List<String> = emptyList()
)
