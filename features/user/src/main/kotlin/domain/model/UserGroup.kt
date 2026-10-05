package domain.model

import model.AppRole
import model.GroupId
import model.UserId

data class UserGroup(
    val userId: UserId,
    val groupId: GroupId,
    val role: AppRole,
    val joinedAt: Long,
)
