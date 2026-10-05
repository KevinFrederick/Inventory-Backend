package api.route

import api.dto.sync.SyncPayloadDto
import api.mapper.toDomain
import api.mapper.toDto
import util.toHttpStatusCode
import api.socket.SyncSocketManager
import domain.usecase.sync.SyncUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.resources.get
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.close
import io.ktor.websocket.send
import kotlinx.serialization.Serializable
import model.GroupId
import model.UserId
import org.koin.ktor.ext.inject
import resource.GroupResource
import result.DomainResult
import usecase.VerifyUserGroupRoleUseCase
import util.userId
import util.verifyGroupAccess
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

@Serializable
@Resource("sync")
class SyncResource (val parent: GroupResource.Id) {
    @Serializable
    @Resource("pull")
    data class Pull(val parent: SyncResource, val updatedAfter: Long = 0L)
}

fun Route.syncRoute() {
    val useCases: SyncUseCase by inject()
    val verifyUserGroupRoleUseCase: VerifyUserGroupRoleUseCase by inject()

    authenticate("auth-jwt") {
        webSocket("/sync/ws/{groupId}") {
            val groupId = call.parameters["groupId"] ?: return@webSocket
            val userId = call.userId

            if (userId == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Unauthorized"))
                return@webSocket
            }

            val roleResult = verifyUserGroupRoleUseCase(
                userId = UserId(userId),
                groupId = GroupId(groupId)
            )

            if (roleResult is DomainResult.Error) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Forbidden"))
                return@webSocket
            }

            val groupSessions = SyncSocketManager.sessions.computeIfAbsent(groupId) {
                Collections.newSetFromMap(ConcurrentHashMap())
            }
            groupSessions.add(this)
            try {
                for (frame in incoming) {
                    // Ignored: App uses REST to push data.
                    // This loop just keeps the connection alive.
                }
            } finally {
                groupSessions.remove(this)
                if (groupSessions.isEmpty()) SyncSocketManager.sessions.remove(groupId)
            }
        }

        rateLimit (RateLimitName("upload_limit")) {
            post<SyncResource> { request ->
                val groupId = GroupId(request.parent.groupId)
                val syncRequest = call.receive<SyncPayloadDto>()
                val syncPayload = syncRequest.toDomain()

                call.verifyGroupAccess(
                    groupId = groupId,
                    verifyUserGroupRole = verifyUserGroupRoleUseCase,
                ) ?: return@post

                when(
                    val result = useCases.syncPush(
                        syncPayload = syncPayload,
                        groupId = groupId,
                    )
                ) {
                    is DomainResult.Success -> {
                        call.respond(HttpStatusCode.OK, result.data.toDto())

                        val targetSessions = SyncSocketManager.sessions[groupId.value] ?: emptySet()
                        targetSessions.forEach { session ->
                            session.send("SYNC_REQUIRED")
                        }
                    }
                    is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
                }
            }
        }

        get<SyncResource.Pull> { request ->
            val groupId = GroupId(request.parent.parent.groupId)
            val updatedAfter = request.updatedAfter

            call.verifyGroupAccess(
                groupId = groupId,
                verifyUserGroupRole = verifyUserGroupRoleUseCase,
            ) ?: return@get

            when(
                val result = useCases.syncPull(
                    updatedAfter = updatedAfter,
                    groupId = groupId,
                )
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.toDto())
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }

        }
    }
}