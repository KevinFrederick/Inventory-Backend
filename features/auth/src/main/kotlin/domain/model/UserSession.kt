package domain.model

import model.User

data class UserSession(
    val user: User,
    val tokens: AuthTokens,
)
