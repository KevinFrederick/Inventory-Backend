package domain.security

interface TokenProvider {
    fun generateAccessToken(userId: String): String
    fun generateRefreshToken(): String
    val refreshTokenValidityMs: Long
}