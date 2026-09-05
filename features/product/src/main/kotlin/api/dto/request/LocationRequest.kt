package api.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class LocationRequest(
    val locationId: String,
    val name: String,
    val description: String? = null,
    val locationBarcode: String? = null,
    val createdAt: Long,
    val lastUpdated: Long
)
