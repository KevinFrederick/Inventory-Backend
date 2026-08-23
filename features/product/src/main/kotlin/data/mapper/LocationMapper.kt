package data.mapper

import data.local.table.LocationTable
import domain.model.Location
import domain.model.LocationId
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toLocation() =
    Location(
        locationId = LocationId(this[LocationTable.locationId]),
        name = this[LocationTable.name],
        description = this[LocationTable.description],
        locationBarcode = this[LocationTable.locationBarcode],
        createdAt = this[LocationTable.createdAt],
        lastUpdated = this[LocationTable.lastUpdated]
    )