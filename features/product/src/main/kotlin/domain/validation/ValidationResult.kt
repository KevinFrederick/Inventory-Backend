package domain.validation

import result.DomainResult
import result.ErrorType

object ValidationResult {
    fun formatResult(errors: List<String>): DomainResult<Unit> {
        return if (errors.isEmpty()) {
            DomainResult.Success(Unit)
        } else {
            DomainResult.Error(errors.joinToString("\n"), ErrorType.BAD_REQUEST)
        }
    }
}