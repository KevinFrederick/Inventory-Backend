package domain.model

@JvmInline
value class LocationId(val value: String)

data class Location(
    val locationId: LocationId,
    val name: String,
    val description: String?,
    val locationBarcode: String?,
    val createdAt: Long,
    val lastUpdated: Long
)
