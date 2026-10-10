package dto

import kotlinx.serialization.Serializable

@Serializable
data class GroupResponse(
    val groupId: String,
    val name: String,
    val description: String?,
    val address: String?,
    val createdAt: Long,
    val lastUpdated: Long
)
