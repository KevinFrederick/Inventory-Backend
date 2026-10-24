package domain.model

import model.AppRole
import model.MembershipStatus
import model.UserId

data class GroupMember(
    val userId: UserId,
    val avatarUrl: String?,
    val name: String,
    val email: String,
    val role: AppRole,
    val status: MembershipStatus,
    val joinedAt: Long
)
