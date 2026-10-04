package api.util

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal

internal val ApplicationCall.userId: String?
    get() = principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString()