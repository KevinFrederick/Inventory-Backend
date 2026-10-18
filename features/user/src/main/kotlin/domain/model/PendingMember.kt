package domain.model

import model.UserId

data class PendingMember(
    val userId: UserId,
    val email: String,
    val name: String,
    val requestedAt: Long
)
