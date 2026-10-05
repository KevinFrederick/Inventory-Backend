package domain.validation

import java.time.ZoneId

object ValidationRulesUser {
    private val PHONE_REGEX = "^\\+?[1-9]\\d{1,14}$".toRegex()
    private val LOCALE_REGEX = "^[a-z]{2}(-[A-Z]{2})?$".toRegex()

    fun <T> validatePhoneNumber(
        item: T,
        getId: (T) -> String,
        getValue: (T) -> String?
    ): List<String> {
        val value = getValue(item)
        if (value.isNullOrBlank()) return emptyList()

        val errors = mutableListOf<String>()
        if (value.length > 32) {
            errors.add(
                "${getId(item)}: Phone number must not exceed 32 characters."
            )
        } else if (!value.matches(PHONE_REGEX)) {
            errors.add(
                "${getId(item)}: Invalid phone format. Use E.164 (e.g., +1234567890)."
            )
        }

        return errors
    }

    fun <T> validateLocale(
        item: T,
        getId: (T) -> String,
        getValue: (T) -> String?
    ): List<String> {
        val value = getValue(item)
        if (value.isNullOrBlank()) return emptyList() // Optional field

        val errors = mutableListOf<String>()
        if (value.length > 10) {
            errors.add(
                "${getId(item)}: Locale must not exceed 10 characters."
            )
        } else if (!value.matches(LOCALE_REGEX)) {
            errors.add(
                "${getId(item)}: Invalid locale format. Use ISO format (e.g., 'en-US' or 'id')."
            )
        }

        return errors
    }

    fun <T> validateTimeZone(
        item: T,
        getId: (T) -> String,
        getValue: (T) -> String?
    ): List<String> {
        val value = getValue(item)
        if (value.isNullOrBlank()) return emptyList()

        val errors = mutableListOf<String>()
        if (value.length > 64) {
            errors.add(
                "${getId(item)}: Time zone must not exceed 64 characters."
            )
        } else {
            val isValid = ZoneId.getAvailableZoneIds().contains(value)
            if (!isValid) {
                errors.add(
                    "${getId(item)}: Invalid time zone. Use IANA format (e.g., 'Asia/Jakarta')."
                )
            }
        }

        return errors
    }
}
