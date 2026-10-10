package api.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class GroupRequest(
    val name: String,
    val description: String?,
    val address: String?,
)
