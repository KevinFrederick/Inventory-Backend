package util

import model.AppRole
import model.GroupId
import usecase.VerifyUserGroupRoleUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import model.UserId
import result.DomainResult

val ApplicationCall.userId: String?
    get() = principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString()


suspend fun ApplicationCall.verifyGroupAccess(
    groupId: GroupId,
    verifyUserGroupRole: VerifyUserGroupRoleUseCase,
    allowedRoles: List<AppRole> = listOf(AppRole.OWNER, AppRole.ADMIN, AppRole.MEMBER)
): AppRole? {
    val userId = this.userId

    if (userId == null) {
        this.respond(HttpStatusCode.Unauthorized)
        return null
    }

    val userRole = when (
        val roleResult = verifyUserGroupRole(
            userId = UserId(userId),
            groupId = groupId
        )
    ) {
        is DomainResult.Success -> roleResult.data
        is DomainResult.Error -> {
            this.respond(HttpStatusCode.Forbidden)
            return null
        }
    }

    if (userRole !in allowedRoles) {
        this.respond(HttpStatusCode.Forbidden)
        return null
    }

    return userRole
}