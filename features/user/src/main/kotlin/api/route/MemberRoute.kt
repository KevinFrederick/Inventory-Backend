package api.route

import api.dto.request.ApproveMemberRequest
import api.dto.request.GenerateInviteRequest
import api.dto.request.JoinGroupRequest
import api.dto.response.InviteResponse
import api.mapper.toResponse
import domain.usecase.membership.MembershipUseCases
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.request.receive
import io.ktor.server.request.receiveNullable
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import kotlinx.serialization.Serializable
import model.AppRole
import model.GroupId
import model.UserId
import org.koin.ktor.ext.inject
import resource.GroupResource
import result.DomainResult
import util.toHttpStatusCode
import util.userId

@Serializable
@Resource("/invites/join")
class JoinGroupResource

fun Route.memberRoute() {
    val membershipUseCases: MembershipUseCases by inject()

    authenticate("auth-jwt") {

        // Join Group using Code
        rateLimit(RateLimitName("upload_limit")) {
            post<JoinGroupResource> {
                val userId = call.userId
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)

                val request = call.receive<JoinGroupRequest>()

                when (val result = membershipUseCases.joinGroupWithCode(UserId(userId), request.code)) {
                    is DomainResult.Success -> call.respond(HttpStatusCode.OK, mapOf("message" to "Join request sent successfully."))
                    is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
                }
            }
        }

        // Generate Invite Code
        rateLimit(RateLimitName("upload_limit")) {
            post<GroupResource.Id.Invites> { resource ->
                val userId = call.userId
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)

                // Allow empty body to use default validityDays
                val request = call.receiveNullable<GenerateInviteRequest>() ?: GenerateInviteRequest()

                when (
                    val result = membershipUseCases.generateInviteCode(
                        requesterId = UserId(userId),
                        groupId = GroupId(resource.parent.groupId),
                        validityDays = request.validityDays
                    )
                ) {
                    is DomainResult.Success -> call.respond(HttpStatusCode.Created, InviteResponse(result.data))
                    is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
                }
            }
        }

        // Get Pending Members
        get<GroupResource.Id.Members.Pending> { resource ->
            val userId = call.userId
                ?: return@get call.respond(HttpStatusCode.Unauthorized)

            when (
                val result = membershipUseCases.getPendingMembers(
                    requesterId = UserId(userId),
                    groupId = GroupId(resource.parent.parent.groupId),
                )
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.map { it.toResponse() })
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }

        // Approve Pending Member
        put<GroupResource.Id.Members.Approve> { resource ->
            val requesterId = call.userId
                ?: return@put call.respond(HttpStatusCode.Unauthorized)

            val request = call.receive<ApproveMemberRequest>()

            when (
                val result = membershipUseCases.approvePendingMember(
                    requesterId = UserId(requesterId),
                    groupId = GroupId(resource.parent.parent.groupId),
                    targetUserId = UserId(resource.targetUserId),
                    assignedRole = AppRole.valueOf(request.role)
                )
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, mapOf("message" to "Member approved."))
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }

        // Reject Pending Member
        delete<GroupResource.Id.Members.Reject> { resource ->
            val requesterId = call.userId
                ?: return@delete call.respond(HttpStatusCode.Unauthorized)

            when (
                val result = membershipUseCases.rejectPendingMember(
                    requesterId = UserId(requesterId),
                    groupId = GroupId(resource.parent.parent.groupId),
                    targetUserId = UserId(resource.targetUserId)
                )
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, mapOf("message" to "Request rejected."))
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }
    }
}