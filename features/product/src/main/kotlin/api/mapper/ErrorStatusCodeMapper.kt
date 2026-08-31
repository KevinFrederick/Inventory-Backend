package api.mapper

import domain.result.ErrorType
import io.ktor.http.HttpStatusCode

fun ErrorType.toHttpStatusCode(): HttpStatusCode {
    return when (this) {
        ErrorType.CONFLICT -> HttpStatusCode.Conflict
        ErrorType.NOT_FOUND -> HttpStatusCode.NotFound
        ErrorType.UNKNOWN -> HttpStatusCode.InternalServerError
        ErrorType.BAD_REQUEST -> HttpStatusCode.BadRequest
    }
}