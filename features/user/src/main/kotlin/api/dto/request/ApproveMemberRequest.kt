package api.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ApproveMemberRequest(
    val role: String,
)
