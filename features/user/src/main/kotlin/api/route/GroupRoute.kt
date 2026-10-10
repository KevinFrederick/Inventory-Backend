package api.route

import api.dto.request.AddMemberRequest
import api.dto.request.GroupRequest
import api.dto.request.UpdateMemberRoleRequest
import api.mapper.toResponse
import util.userId
import model.AppRole
import model.GroupId
import domain.usecase.group.GroupUseCases
import domain.usecase.user_group.UserGroupUseCases
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.routing.Route
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.resources.get
import io.ktor.server.resources.put
import model.UserId
import org.koin.ktor.ext.inject
import resource.GroupResource
import result.DomainResult
import util.toHttpStatusCode

fun Route.groupRoute() {
    val groupUseCases: GroupUseCases by inject()
    val userGroupUseCases: UserGroupUseCases by inject()

    authenticate("auth-jwt") {
        // Create Group
        rateLimit (RateLimitName("upload_limit")) {
            post<GroupResource>{
                val requesterId = call.userId
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)

                val groupRequest = call.receive<GroupRequest>()

                when(
                    val result = groupUseCases.insertGroup(
                        creatorId = UserId(requesterId),
                        name = groupRequest.name,
                        description = groupRequest.description,
                        address = groupRequest.address,
                    )
                ) {
                    is DomainResult.Success -> call.respond(HttpStatusCode.Created, result.data.toResponse())
                    is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
                }
            }
        }

        // Add user to group
        rateLimit (RateLimitName("upload_limit")) {
            post<GroupResource.Id.Members>{ request ->
                val requesterId = call.userId
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val groupId = request.parent.groupId

                val memberRequest = call.receive<AddMemberRequest>()
                val assignRole = runCatching { AppRole.valueOf(memberRequest.role) }
                    .getOrDefault(AppRole.MEMBER)

                when(
                    val result = userGroupUseCases.addUserToGroup(
                        requesterId = UserId(requesterId),
                        groupId = GroupId(groupId),
                        targetEmail = memberRequest.email,
                        assignRole = assignRole
                    )
                ) {
                    is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.toResponse())
                    is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
                }
            }
        }

        // Get group by id
        get<GroupResource.Id> {request ->
            val requesterId = call.userId
                ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val groupId = request.groupId

            when(
                val result = groupUseCases.getGroupById(
                    requesterId = UserId(requesterId),
                    groupId = GroupId(groupId)
                )
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.toResponse())
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }

        // Get group for user
        get<GroupResource>{
            val requesterId = call.userId
                ?: return@get call.respond(HttpStatusCode.Unauthorized)

            when(
                val result = userGroupUseCases.getGroupsForUser(UserId(requesterId))
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.map { it.toResponse() })
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }

        // Get members for group
        get<GroupResource.Id.Members> { request ->
            val requesterId = call.userId
                ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val groupId = request.parent.groupId

            when(
                val result = userGroupUseCases.getGroupMembers(
                    requesterId = UserId(requesterId),
                    groupId = GroupId(groupId)
                )
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.map { it.toResponse() })
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }

        // Update group
        put<GroupResource.Id> {request ->
            val requesterId = call.userId
                ?: return@put call.respond(HttpStatusCode.Unauthorized)

            val groupId = request.groupId
            val groupRequest = call.receive<GroupRequest>()

            when(
                val result = groupUseCases.updateGroup(
                    groupId = GroupId(groupId),
                    requesterId = UserId(requesterId),
                    name = groupRequest.name,
                    description = groupRequest.description,
                    address = groupRequest.address,
                )
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.toResponse())
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }

        // Update member role
        put<GroupResource.Id.Members.Member> {request ->
            val requesterId = call.userId
                ?: return@put call.respond(HttpStatusCode.Unauthorized)

            val groupId = request.parent.parent.groupId
            val targetedUserId = request.memberId

            val memberRequest = call.receive<UpdateMemberRoleRequest>()

            val newRole = runCatching { AppRole.valueOf(memberRequest.role) }
                .getOrElse {
                    return@put call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "Invalid role. Must be OWNER, ADMIN, or MEMBER.")
                    )
                }

            when(
                val result = userGroupUseCases.updateMemberRole(
                    requesterId = UserId(requesterId),
                    targetUserId = UserId(targetedUserId),
                    groupId = GroupId(groupId),
                    newRole = newRole
                )
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, "Successfully updated")
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }

        // Delete group
        delete<GroupResource.Id> { request ->
            val requesterId = call.userId
                ?: return@delete call.respond(HttpStatusCode.Unauthorized)

            val groupId = request.groupId

            when(
                val result = groupUseCases.deleteGroup(
                    groupId = GroupId(groupId),
                    requesterId = UserId(requesterId),
                )
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.NoContent)
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }

        // Remove member
        delete<GroupResource.Id.Members.Member> { request ->
            val requesterId = call.userId
                ?: return@delete call.respond(HttpStatusCode.Unauthorized)

            val groupId = request.parent.parent.groupId
            val targetedUserId = request.memberId

            when(
                val result = userGroupUseCases.removeMember(
                    requesterId = UserId(requesterId),
                    targetUserId = UserId(targetedUserId),
                    groupId = GroupId(groupId)
                )
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.NoContent)
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }
    }
}