package domain.model

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class LocationId(val value: String)

@Serializable
data class Location(
    val locationId: LocationId,
    val name: String,
    val description: String?,
    val locationBarcode: String?,
    val createdAt: Long,
    val lastUpdated: Long
)
