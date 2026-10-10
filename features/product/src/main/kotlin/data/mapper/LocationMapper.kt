package data.mapper

import data.table.product.LocationTable
import domain.model.Location
import domain.model.LocationId
import model.GroupId
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toLocation() =
    Location(
        locationId = LocationId(this[LocationTable.locationId]),
        groupId = GroupId(this[LocationTable.groupId]),
        name = this[LocationTable.name],
        description = this[LocationTable.description],
        locationBarcode = this[LocationTable.locationBarcode],
        createdAt = this[LocationTable.createdAt],
        lastUpdated = this[LocationTable.lastUpdated]
    )