package domain.validation

import domain.model.Group
import result.DomainResult
import validation.ValidationResult
import validation.ValidationRules

class GroupValidator {
    fun validateGroup(group: Group): DomainResult<Unit> {
        val errors = validateFields(
            item = group,
            getId = { it.groupId.value },
            getName = { it.name },
            getDescription = { it.description },
            getAddress = { it.address },
            getCreatedAt = { it.createdAt },
            getLastUpdated = { it.lastUpdated }
        )

        return ValidationResult.formatResult(errors)
    }

    private fun <T> validateFields(
        item: T,
        getId: (T) -> String,
        getName: (T) -> String,
        getDescription: (T) -> String?,
        getAddress: (T) -> String?,
        getCreatedAt: (T) -> Long,
        getLastUpdated: (T) -> Long,
    ): List<String> {
        val errors = mutableListOf<String>()

        errors.addAll(
            ValidationRules.validateStringLengthAndFormat(
                item = item,
                getId = getId,
                getFieldName = { "Group Name" },
                getValue = getName,
                maxLength = 255,
                allowNewLines = false,
                allowSpace = true
            )
        )

        errors.addAll(
            ValidationRules.validateStringLengthAndFormat(
                item = item,
                getId = getId,
                getFieldName = { "Description" },
                getValue = getDescription,
                maxLength = 2000,
                allowNewLines = true,
                allowSpace = true
            )
        )

        errors.addAll(
            ValidationRules.validateStringLengthAndFormat(
                item = item,
                getId = getId,
                getFieldName = { "Address" },
                getValue = getAddress,
                maxLength = 500,
                allowNewLines = true,
                allowSpace = true
            )
        )

        errors.addAll(
            ValidationRules.validateTimestamps(
                item = item,
                getId = getId,
                getCreatedAt = getCreatedAt,
                getLastUpdated = getLastUpdated
            )
        )

        return errors
    }
}