package api.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class PendingMemberResponse(
    val userId: String,
    val email: String,
    val name: String,
    val requestedAt: Long
)
