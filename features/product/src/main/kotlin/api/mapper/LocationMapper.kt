package api.mapper

import api.dto.request.LocationRequest
import api.dto.response.LocationResponse
import domain.model.Location
import domain.model.LocationId
import util.cleanInlineSpaces
import util.toTitleCase

fun LocationRequest.toDomain(): Location =
    Location(
        locationId = LocationId(this.locationId),
        name = this.name.cleanInlineSpaces().toTitleCase(),
        description = this.description?.cleanInlineSpaces(),
        locationBarcode = this.locationBarcode?.trim(),
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated
    )

fun Location.toResponse(): LocationResponse =
    LocationResponse(
        locationId = this.locationId.value,
        name = this.name,
        description = this.description,
        locationBarcode = this.locationBarcode,
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated
    )