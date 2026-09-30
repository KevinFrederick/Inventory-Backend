package domain.validation

fun <T> validateBarcodeAsciiNoSpaces(
    item: T,
    getId: (T) -> String,
    getFieldName: () -> String,
    getValue: (T) -> String?,
): List<String> {
    val errors = mutableListOf<String>()
    val value = getValue(item) ?: return emptyList()

    if (!value.matches(Regex("^[\\x21-\\x7E]+\$"))) {
        errors.add(
            "${getId(item)}: ${getFieldName()} contains invalid characters or spaces"
        )
    }

    return errors
}

fun <T> validateExpirationDate(
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