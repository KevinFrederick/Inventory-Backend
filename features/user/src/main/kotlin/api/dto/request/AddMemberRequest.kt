package api.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class AddMemberRequest(
    val email: String,
    val role: String
)
