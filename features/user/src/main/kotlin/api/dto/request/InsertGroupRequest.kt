package api.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class InsertGroupRequest(
    val name: String ,
    val description: String? = null,
    val address: String? = null,
)
