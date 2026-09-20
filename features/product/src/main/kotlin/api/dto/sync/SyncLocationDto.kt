package api.dto.sync

import kotlinx.serialization.Serializable

@Serializable
data class SyncLocationDto(
    val locationId: String,
    val name: String,
    val description: String? = null,
    val locationBarcode: String? = null,
    val createdAt: Long,
    val lastUpdated: Long
)
