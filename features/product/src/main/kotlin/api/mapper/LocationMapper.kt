package api.mapper

import api.dto.request.LocationRequest
import api.dto.response.LocationResponse
import domain.model.Location
import domain.model.LocationId
import model.GroupId
import util.cleanInlineSpaces
import util.toTitleCase

fun LocationRequest.toDomain(groupId: GroupId): Location =
    Location(
        locationId = LocationId(this.locationId),
        groupId = groupId,
        name = this.name.cleanInlineSpaces().toTitleCase(),
        description = this.description?.cleanInlineSpaces(),
        locationBarcode = this.locationBarcode?.trim(),
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated
    )

fun Location.toResponse(): LocationResponse =
    LocationResponse(
        locationId = this.locationId.value,
        groupId = this.groupId.value,
        name = this.name,
        description = this.description,
        locationBarcode = this.locationBarcode,
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated
    )