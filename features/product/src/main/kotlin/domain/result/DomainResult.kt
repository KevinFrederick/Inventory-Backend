package domain.result

sealed class DomainResult<out T> {
    data class Success <T>(val data: T) : DomainResult<T>()
    data class Error(val message: String, val errorType: ErrorType) : DomainResult<Nothing>()
}