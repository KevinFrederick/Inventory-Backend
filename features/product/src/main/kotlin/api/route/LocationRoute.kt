package api.route

import api.dto.request.LocationRequest
import api.mapper.toDomain
import api.mapper.toHttpStatusCode
import api.mapper.toResponse
import domain.model.LocationId
import result.DomainResult
import domain.usecase.location.LocationUseCases
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.routing.Route
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
@Resource("/location")
class LocationResource {
    @Serializable
    @Resource("{id}")
    class Id (val parent: LocationResource = LocationResource(), val id: String)
}

fun Route.locationRoutes() {
    val useCases: LocationUseCases by inject()

    get <LocationResource> {
        when(
            val result = useCases.getAllLocation()
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.map { it.toResponse() })
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }
    }

    get <LocationResource.Id> { request ->
        val locationId = LocationId(request.id)

        when(
            val result = useCases.getLocationById(locationId)
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.toResponse())
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }
    }

    rateLimit (RateLimitName("upload_limit")) {
        post <LocationResource> {
            val locationRequest = call.receive<LocationRequest>()
            val location = locationRequest.toDomain()

            when(
                val result = useCases.insertLocation(location)
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.Created, location.toResponse())
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }
    }

    rateLimit (RateLimitName("upload_limit")) {
        put <LocationResource.Id> { request ->
            val locationId = LocationId(request.id)
            val locationRequest = call.receive<LocationRequest>()
            val updatedLocation = locationRequest.toDomain()

            when (
                val result = useCases.updateLocation(locationId, updatedLocation)
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, updatedLocation.toResponse())
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }
    }

    delete <LocationResource.Id> { request ->
        val locationId = LocationId(request.id)

        when(
            val result = useCases.deleteLocation(locationId)
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.NoContent)
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }
    }
}