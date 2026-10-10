package api.route

import api.dto.request.LocationRequest
import api.mapper.toDomain
import util.toHttpStatusCode
import api.mapper.toResponse
import domain.model.LocationId
import result.DomainResult
import domain.usecase.location.LocationUseCases
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.auth.authenticate
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
import model.AppRole
import model.GroupId
import org.koin.ktor.ext.inject
import resource.GroupResource
import usecase.VerifyUserGroupRoleUseCase
import util.verifyGroupAccess

@Serializable
@Resource("location")
class LocationResource (val parent: GroupResource.Id) {
    @Serializable
    @Resource("{locationId}")
    class Id (val parent: LocationResource, val locationId: String)
}

fun Route.locationRoutes() {
    val useCases: LocationUseCases by inject()
    val verifyUserGroupRoleUseCase: VerifyUserGroupRoleUseCase by inject()

    authenticate("auth-jwt") {
        // Get all locations for current group
        get <LocationResource> { request ->
            val groupId = GroupId(request.parent.groupId)

            call.verifyGroupAccess(
                groupId = groupId,
                verifyUserGroupRole = verifyUserGroupRoleUseCase,
            ) ?: return@get

            when(
                val result = useCases.getAllLocation(
                    groupId = groupId,
                )
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.map { it.toResponse() })
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }

        // Get location
        get <LocationResource.Id> { request ->
            val groupId = GroupId(request.parent.parent.groupId)
            val locationId = LocationId(request.locationId)

            call.verifyGroupAccess(
                groupId = groupId,
                verifyUserGroupRole = verifyUserGroupRoleUseCase,
            ) ?: return@get

            when(
                val result = useCases.getLocationById(
                    locationId = locationId,
                    groupId = groupId,
                )
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.toResponse())
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }

        // Insert Location
        rateLimit (RateLimitName("upload_limit")) {
            post <LocationResource> { request ->
                val groupId = GroupId(request.parent.groupId)

                call.verifyGroupAccess(
                    groupId = groupId,
                    verifyUserGroupRole = verifyUserGroupRoleUseCase,
                    allowedRoles = listOf(AppRole.OWNER, AppRole.ADMIN)
                ) ?: return@post

                val locationRequest = call.receive<LocationRequest>()
                val location = locationRequest.toDomain(groupId)

                when(
                    val result = useCases.insertLocation(
                        location = location,
                        groupId = groupId,
                    )
                ) {
                    is DomainResult.Success -> call.respond(HttpStatusCode.Created, location.toResponse())
                    is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
                }
            }
        }

        // Update location
        rateLimit (RateLimitName("upload_limit")) {
            put <LocationResource.Id> { request ->
                val groupId = GroupId(request.parent.parent.groupId)

                call.verifyGroupAccess(
                    groupId = groupId,
                    verifyUserGroupRole = verifyUserGroupRoleUseCase,
                    allowedRoles = listOf(AppRole.OWNER, AppRole.ADMIN)
                ) ?: return@put

                val locationId = LocationId(request.locationId)
                val locationRequest = call.receive<LocationRequest>()
                val updatedLocation = locationRequest.toDomain(groupId)

                when (
                    val result = useCases.updateLocation(
                        locationId = locationId,
                        location = updatedLocation,
                        groupId = groupId,
                    )
                ) {
                    is DomainResult.Success -> call.respond(HttpStatusCode.OK, updatedLocation.toResponse())
                    is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
                }
            }
        }

        // Delete location
        delete <LocationResource.Id> { request ->
            val groupId = GroupId(request.parent.parent.groupId)
            val locationId = LocationId(request.locationId)

            call.verifyGroupAccess(
                groupId = groupId,
                verifyUserGroupRole = verifyUserGroupRoleUseCase,
                allowedRoles = listOf(AppRole.OWNER, AppRole.ADMIN)
            ) ?: return@delete

            when(
                val result = useCases.deleteLocation(
                    locationId = locationId,
                    groupId = groupId,
                )
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.NoContent)
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }
    }
}