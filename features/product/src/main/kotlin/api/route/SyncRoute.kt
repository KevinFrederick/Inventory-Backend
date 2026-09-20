package api.route

import api.dto.sync.SyncPayloadDto
import api.mapper.toDomain
import api.mapper.toDto
import api.mapper.toHttpStatusCode
import domain.usecase.sync.SyncUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.resources.get
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import result.DomainResult

@Serializable
@Resource("/sync")
class SyncResource {
    @Serializable
    @Resource("pull")
    data class Pull(val parent: SyncResource, val updatedAfter: Long = 0L)
}

fun Route.syncRoute() {
    val useCases: SyncUseCase by inject()

    rateLimit (RateLimitName("upload_limit")) {
        post<SyncResource> {
            val syncRequest = call.receive<SyncPayloadDto>()
            val syncPayload = syncRequest.toDomain()

            when(
                val result = useCases.syncPush(syncPayload)
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.toDto())
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }
    }

    get<SyncResource.Pull> { request ->
        val updatedAfter = request.updatedAfter

        when(
            val result = useCases.syncPull(updatedAfter)
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.toDto())
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }

    }
}