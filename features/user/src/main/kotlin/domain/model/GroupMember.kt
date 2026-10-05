package domain.model

import model.AppRole
import model.UserId

data class GroupMember(
    val userId: UserId,
    val name: String,
    val email: String,
    val role: AppRole,
    val joinedAt: Long
)
