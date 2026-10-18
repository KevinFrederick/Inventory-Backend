package api.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class JoinGroupRequest(
    val code: String
)
