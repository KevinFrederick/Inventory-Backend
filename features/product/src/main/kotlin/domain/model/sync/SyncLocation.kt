package domain.model.sync

import domain.model.LocationId

data class SyncLocation(
    val locationId: LocationId,
    val name: String,
    val description: String? = null,
    val locationBarcode: String? = null,
    val createdAt: Long,
    val lastUpdated: Long
)
