package domain.model

import model.GroupWithRole
import model.User

data class UserSession(
    val user: User,
    val groups: List<GroupWithRole>,
    val tokens: AuthTokens,
)
