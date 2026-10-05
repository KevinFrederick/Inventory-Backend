package domain.validation

import model.User
import result.DomainResult
import validation.ValidationResult
import validation.ValidationRules

class UserValidator {
    fun validateUser(user: User): DomainResult<Unit> {
        val errors = validateFields(
            item = user,
            getId = { it.userId.value },
            getName = { it.name },
            getEmail = { it.email },
            getAvatarUrl = { it.avatarUrl },
            getPhoneNumber = { it.phoneNumber },
            getJobTitle = { it.jobTitle },
            getLocale = { it.locale },
            getTimezone = { it.timeZone },
            getCreatedAt = { it.createdAt },
            getLastUpdated = { it.lastUpdated }
        )

        return ValidationResult.formatResult(errors)
    }

    private fun <T> validateFields(
        item: T,
        getId: (T) -> String,
        getName: (T) -> String,
        getEmail: (T) -> String,
        getAvatarUrl: (T) -> String?,
        getPhoneNumber: (T) -> String?,
        getJobTitle: (T) -> String?,
        getLocale: (T) -> String?,
        getTimezone: (T) -> String?,
        getCreatedAt: (T) -> Long,
        getLastUpdated: (T) -> Long,
    ): List<String> {
        val errors = mutableListOf<String>()

        errors.addAll(
            ValidationRules.validateStringLengthAndFormat(
                item = item,
                getId = getId,
                getFieldName = { "Name" },
                getValue = getName,
                maxLength = 255,
                allowNewLines = false,
                allowSpace = true
            )
        )

        errors.addAll(
            ValidationRules.validateEmail(
                item = item,
                getId = getId,
                getValue = getEmail
            )
        )

        errors.addAll(
            ValidationRules.validateStringLengthAndFormat(
                item = item,
                getId = getId,
                getFieldName = { "Avatar Url" },
                getValue = getAvatarUrl,
                maxLength = 256,
                allowNewLines = false,
                allowSpace = false
            )
        )

        errors.addAll(
            ValidationRulesUser.validatePhoneNumber(
                item = item,
                getId = getId,
                getValue = getPhoneNumber
            )
        )

        errors.addAll(
            ValidationRules.validateStringLengthAndFormat(
                item = item,
                getId = getId,
                getFieldName = { "Job Title" },
                getValue = getJobTitle,
                maxLength = 128,
                allowNewLines = false,
                allowSpace = true
            )
        )

        errors.addAll(
            ValidationRulesUser.validateLocale(
                item = item,
                getId = getId,
                getValue = getLocale
            )
        )

        errors.addAll(
            ValidationRulesUser.validateTimeZone(
                item = item,
                getId = getId,
                getValue = getTimezone
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