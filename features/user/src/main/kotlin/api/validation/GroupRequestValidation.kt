package api.validation

import api.dto.request.GroupRequest
import io.ktor.server.plugins.requestvalidation.RequestValidationConfig
import io.ktor.server.plugins.requestvalidation.ValidationResult

fun RequestValidationConfig.groupValidation() {
    validate<GroupRequest> { request ->
        val errors = mutableListOf<String>()

        if (request.name.isBlank()) errors.add("Name is required")

        if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)
    }
}