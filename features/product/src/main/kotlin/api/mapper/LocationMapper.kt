package api.mapper

import api.dto.request.LocationRequest
import api.dto.response.LocationResponse
import domain.model.Location
import domain.model.LocationId

fun LocationRequest.toDomain(): Location =
    Location(
        locationId = LocationId(this.locationId),
        name = this.name,
        description = this.description,
        locationBarcode = this.locationBarcode,
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