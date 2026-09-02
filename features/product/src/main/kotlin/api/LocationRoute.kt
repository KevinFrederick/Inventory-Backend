package api

import api.mapper.toHttpStatusCode
import domain.model.Location
import domain.model.LocationId
import domain.result.DomainResult
import domain.usecase.location.LocationUseCases
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
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
            is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }
    }

    get <LocationResource.Id> { request ->
        val locationId = LocationId(request.id)

        when(
            val result = useCases.getLocationById(locationId)
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }
    }

    post <LocationResource> {
        val location = call.receive<Location>()

        when(
            val result = useCases.insertLocation(location)
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.Created, location)
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }
    }

    put <LocationResource.Id> { request ->
        val locationId = LocationId(request.id)
        val updatedLocation = call.receive<Location>()

        when (
            val result = useCases.updateLocation(locationId, updatedLocation)
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.OK, updatedLocation)
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
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