package domain.model

import model.GroupId

@JvmInline
value class LocationId(val value: String)

data class Location(
    val locationId: LocationId,
    val groupId: GroupId,
    val name: String,
    val description: String?,
    val locationBarcode: String?,
    val createdAt: Long,
    val lastUpdated: Long
)
