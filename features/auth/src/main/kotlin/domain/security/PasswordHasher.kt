package domain.security

interface PasswordHasher {
    fun hashPassword(password: String): String
    fun verify(password: String, hash: String): Boolean
}