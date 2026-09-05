package api.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CategoryRequest(
    val categoryId: String,
    val name: String,
    val description: String? = null,
    val createdAt: Long,
    val lastUpdated: Long
)
