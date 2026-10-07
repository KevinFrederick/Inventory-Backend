package dto

import kotlinx.serialization.Serializable

@Serializable
data class UserGroupResponse(
    val group: GroupResponse,
    val role: String
)
