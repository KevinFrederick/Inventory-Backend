package api.validation

import api.dto.request.LocationRequest

fun LocationRequest.validate(): List<String> {
    val errors = mutableListOf<String>()

    if (locationId.isBlank()) errors.add("LocationId cannot be empty")
    if (name.isBlank()) errors.add("Name cannot be empty")
    if (createdAt < 0L) errors.add("Invalid createdAt timestamp")
    if (lastUpdated < 0L) errors.add("Invalid last updated")

    return errors
}