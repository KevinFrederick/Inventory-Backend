package api.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class GenerateInviteRequest(
    val validityDays: Int = 7,
)
