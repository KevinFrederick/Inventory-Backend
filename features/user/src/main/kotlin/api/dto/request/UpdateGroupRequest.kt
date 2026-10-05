package api.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateGroupRequest(
    val name: String? = null,
    val description: String? = null,
    val address: String? = null,
)
