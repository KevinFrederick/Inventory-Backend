package com.kevinfreyap

import api.security.JwtConfig
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val jwtConfig: JwtConfig by inject()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = "Ktor App"
            verifier(jwtConfig.makeVerifier())
            validate { credential ->
                if (credential.payload.getClaim("userId").asString() != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    "Access token is invalid or expired"
                )
            }
        }
    }
}