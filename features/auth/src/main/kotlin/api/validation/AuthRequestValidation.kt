package api.validation

import api.dto.request.LoginRequest
import api.dto.request.RegisterRequest
import io.ktor.server.plugins.requestvalidation.RequestValidationConfig
import io.ktor.server.plugins.requestvalidation.ValidationResult

fun RequestValidationConfig.registerValidation() {
    validate<RegisterRequest> { request ->
        val errors = request.validate()

        if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)
    }
}

fun RequestValidationConfig.loginValidation() {
    validate<LoginRequest> { request ->
        val errors = request.validate()

        if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)
    }
}