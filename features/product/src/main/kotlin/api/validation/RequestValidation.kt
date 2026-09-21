package api.validation

import api.dto.request.CategoryRequest
import api.dto.request.LocationRequest
import api.dto.request.ProductRequest
import api.dto.request.StockBatchRequest
import api.dto.sync.SyncPayloadDto
import io.ktor.server.plugins.requestvalidation.RequestValidationConfig
import io.ktor.server.plugins.requestvalidation.ValidationResult

fun RequestValidationConfig.productValidation() {
    validate<ProductRequest> { request ->
        val errors = request.validate()

        if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)
    }
}

fun RequestValidationConfig.batchValidation() {
    validate<StockBatchRequest> { request ->
        val errors = request.validate()

        if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)
    }
}

fun RequestValidationConfig.categoryValidation() {
    validate<CategoryRequest> { request ->
        val errors = request.validate()

        if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)
    }
}

fun RequestValidationConfig.locationValidation() {
    validate<LocationRequest> { request ->
        val errors = request.validate()

        if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)
    }
}

fun RequestValidationConfig.syncValidation() {
    validate<SyncPayloadDto> { request ->
        val errors = request.validate()

        if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)
    }
}