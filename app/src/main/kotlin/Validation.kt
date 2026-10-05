package com.kevinfreyap

import api.validation.batchValidation
import api.validation.categoryValidation
import api.validation.groupValidation
import api.validation.locationValidation
import api.validation.loginValidation
import api.validation.productValidation
import api.validation.registerValidation
import api.validation.syncValidation
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.SerializationException

fun Application.configureValidation() {
    install(RequestValidation) {
        productValidation()
        batchValidation()
        categoryValidation()
        locationValidation()
        syncValidation()

        registerValidation()
        loginValidation()
        groupValidation()
    }

    install(StatusPages) {
        exception <RequestValidationException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("errors" to cause.reasons)
            )
        }

        exception <SerializationException> { call, _ ->
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("errors" to "Invalid JSON format or Missing required field")
            )
        }
    }
}