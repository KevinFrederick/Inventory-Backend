package api.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class LocationResponse(
    val locationId: String,
    val name: String,
    val description: String?,
    val locationBarcode: String?,
    val createdAt: Long,
    val lastUpdated: Long
)
