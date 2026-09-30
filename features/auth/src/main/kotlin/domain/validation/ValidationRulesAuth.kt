package domain.validation

object ValidationRulesAuth {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$")

    private const val MAX_EMAIL_LENGTH = 255
    private const val MIN_PASSWORD_LENGTH = 8

    private const val MAX_PASSWORD_LENGTH = 72

    fun validateEmail(
        email: String,
    ): List<String> {
        val errors = mutableListOf<String>()
        val trimmed = email.trim()

        if (trimmed.length > MAX_EMAIL_LENGTH) {
            errors.add("Email cannot exceed $MAX_EMAIL_LENGTH characters.")
        }

        if (!trimmed.matches(EMAIL_REGEX)) {
            errors.add(
                "Invalid email format"
            )
        }

        return errors
    }

    fun validatePassword(
        password: String
    ): List<String> {
        val errors = mutableListOf<String>()

        if (password.length < MIN_PASSWORD_LENGTH) {
            errors.add("Password must be at least $MIN_PASSWORD_LENGTH characters long.")
        }

        if (password.length > MAX_PASSWORD_LENGTH) {
            errors.add("Password cannot exceed $MAX_PASSWORD_LENGTH characters.")
        }

        if (password.contains(" ")) {
            errors.add("Password cannot contain spaces.")
        }

        if (!password.any { it.isDigit() }) {
            errors.add("Password must contain at least one numeric digit (0-9).")
        }

        if (!password.any { it.isLetter() }) {
            errors.add("Password must contain at least one letter.")
        }

        return errors
    }
}
