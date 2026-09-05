package api.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    val categoryId: String,
    val name: String,
    val description: String?,
    val createdAt: Long,
    val lastUpdated: Long
)
