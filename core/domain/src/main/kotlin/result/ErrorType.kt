package result

enum class ErrorType {
    CONFLICT,
    BAD_REQUEST,
    NOT_FOUND,

    UNAUTHORIZED,
    FORBIDDEN,
    UNKNOWN
}