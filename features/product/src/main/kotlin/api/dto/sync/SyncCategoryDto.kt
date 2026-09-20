package api.dto.sync

import kotlinx.serialization.Serializable

@Serializable
data class SyncCategoryDto(
    val categoryId: String,
    val name: String,
    val description: String? = null,
    val createdAt: Long,
    val lastUpdated: Long
)
