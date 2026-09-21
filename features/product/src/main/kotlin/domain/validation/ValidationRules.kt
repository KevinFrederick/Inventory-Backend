package domain.validation

class ValidationRules {
    inline fun <T> validateStringLengthAndFormat(
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

    inline fun <T> validateNumericOnly(
        item: T,
        getId: (T) -> String,
        getFieldName: () -> String,
        getValue: (T) -> String?,
    ): List<String> {
        val errors = mutableListOf<String>()
        val value = getValue(item) ?: return emptyList()

        if (!value.all { it.isDigit() }) {
            errors.add(
                "${getId(item)}: ${getFieldName()} must contain only digits"
            )
        }

        return errors
    }

    inline fun <T> validateTimestamps(
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

    inline fun <T> validateExpirationDate(
        item: T,
        getId: (T) -> String,
        getCreatedAt: (T) -> Long,
        getExpirationDate: (T) -> Long?
    ): List<String> {
        val errors = mutableListOf<String>()
        val createdAt = getCreatedAt(item)
        val expirationDate = getExpirationDate(item)

        if (expirationDate != null && expirationDate < createdAt) {
            errors.add(
                "${getId(item)}: expiration date ($expirationDate) cannot be earlier than creation date ($createdAt)"
            )
        }

        return errors
    }

}