package api.route

import api.dto.request.UpdateProfileRequest
import api.mapper.toResponse
import api.util.userId
import domain.usecase.user.UserUseCases
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.resources.get
import io.ktor.server.resources.put
import kotlinx.serialization.Serializable
import model.UserId
import org.koin.ktor.ext.inject
import result.DomainResult
import util.toHttpStatusCode

@Serializable
@Resource("/me")
class UserResource

fun Route.userRoute() {
    val useCases: UserUseCases by inject()

    authenticate("auth-jwt") {
        get<UserResource> {
            val userId = call.userId
                ?: return@get call.respond(HttpStatusCode.Unauthorized)

            when(
                val result = useCases.getUserById(UserId(userId))
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.toResponse())
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }

        rateLimit (RateLimitName("upload_limit")) {
            put<UserResource> {
                val userId = call.userId
                    ?: return@put call.respond(HttpStatusCode.Unauthorized)

                val userRequest = call.receive<UpdateProfileRequest>()

                when(
                    val result = useCases.updateUser(
                        userId = UserId(userId),
                        name = userRequest.name,
                        avatarUrl = userRequest.avatarUrl,
                        phoneNumber = userRequest.phoneNumber,
                        jobTitle = userRequest.jobTitle,
                        locale = userRequest.locale,
                        timeZone = userRequest.timeZone,
                        lastUpdated = userRequest.lastUpdated,
                    )
                ) {
                    is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.toResponse())
                    is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
                }
            }
        }

        delete<UserResource> {
            val userId = call.userId
                ?: return@delete call.respond(HttpStatusCode.Unauthorized)

            when(
                val result = useCases.deleteUser(UserId(userId))
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.NoContent)
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }
    }
}