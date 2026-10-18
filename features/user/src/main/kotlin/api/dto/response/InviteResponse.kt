package api.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class InviteResponse(
    val code: String,
)
