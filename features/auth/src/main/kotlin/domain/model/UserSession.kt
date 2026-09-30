package domain.model

data class UserSession(
    val user: User,
    val tokens: AuthTokens,
)
