package api.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class GroupMemberResponse(
    val userId: String,
    val name: String,
    val email: String,
    val role: String,
    val joinedAt: Long,
)
