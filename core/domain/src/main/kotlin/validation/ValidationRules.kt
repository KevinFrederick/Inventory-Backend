package validation

object ValidationRules {
    private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]+$".toRegex()
    private const val MAX_EMAIL_LENGTH = 255
    private const val MIN_PASSWORD_LENGTH = 8

    private const val MAX_PASSWORD_LENGTH = 72

    fun <T> validateStringLengthAndFormat(
        item: T,
        getId: (T) -> String,
        getFieldName: () -> String,
        getValue: (T) -> String?,
        maxLength: Int,
        allowNewLines: Boolean = false,
        allowSpace: Boolean = true,
    ): List<String> {
        val errors = mutableListOf<String>()
        val value = getValue(item) ?: return emptyList()
        val fieldName = getFieldName()
        val id = getId(item)

        if (value.length > maxLength) {
            errors.add(
                "$id: $fieldName cannot exceed $maxLength characters. Current length is ${value.length}"
            )
        }

        if (!allowNewLines && (value.contains("\n") || value.contains("\r"))) {
            errors.add(
                "$id: $fieldName cannot contains newline characters"
            )
        }

        if (!allowSpace && (value.contains(Regex("\\s")))) {
            errors.add(
                "$id: $fieldName cannot contains spaces"
            )
        }

        return errors
    }

    fun <T> validateTimestamps(
        item: T,
        getId: (T) -> String,
        getCreatedAt: (T) -> Long,
        getLastUpdated: (T) -> Long,
    ): List<String> {
        val errors = mutableListOf<String>()
        val createdAt = getCreatedAt(item)
        val lastUpdated = getLastUpdated(item)

        if (lastUpdated < createdAt) {
            errors.add(
                "${getId(item)}: lastUpdated ($lastUpdated) cannot be earlier than createdAt ($createdAt)"
            )
        }

        return errors
    }

    fun <T> validateEmail(
        item: T,
        getId: (T) -> String,
        getValue: (T) -> String
    ): List<String> {
        val value = getValue(item)
        val errors = mutableListOf<String>()

        if (value.isBlank()) {
            errors.add(
                "${getId(item)}: Email cannot be empty."
            )
        } else if (value.length > MAX_EMAIL_LENGTH) {
            errors.add(
                "${getId(item)}: Email must not exceed $MAX_EMAIL_LENGTH characters."
            )
        } else if (!value.matches(EMAIL_REGEX)) {
            errors.add(
                "${getId(item)}: Invalid email format."
            )
        }

        return errors
    }

    fun <T> validatePassword(
        item: T,
        getId: (T) -> String,
        getValue: (T) -> String
    ): List<String> {
        val password = getValue(item)
        val errors = mutableListOf<String>()

        if (password.length < MIN_PASSWORD_LENGTH) {
            errors.add("${getId(item)}: Password must be at least $MIN_PASSWORD_LENGTH characters long.")
        }

        if (password.length > MAX_PASSWORD_LENGTH) {
            errors.add("${getId(item)}: Password cannot exceed $MAX_PASSWORD_LENGTH characters.")
        }

        if (password.contains(" ")) {
            errors.add("${getId(item)}: Password cannot contain spaces.")
        }

        if (!password.any { it.isDigit() }) {
            errors.add("${getId(item)}: Password must contain at least one numeric digit (0-9).")
        }

        if (!password.any { it.isLetter() }) {
            errors.add("${getId(item)}: Password must contain at least one letter.")
        }

        return errors
    }
}