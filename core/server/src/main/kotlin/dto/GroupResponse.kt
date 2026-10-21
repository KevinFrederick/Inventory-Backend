package dto

import kotlinx.serialization.Serializable

@Serializable
data class GroupResponse(
    val groupId: String,
    val name: String,
    val description: String?,
    val address: String?,
    val totalMembers: Int,
    val totalProducts: Int,
    val createdAt: Long,
    val lastUpdated: Long
)
