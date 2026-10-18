package domain.model

import model.GroupId

data class InviteDetail(
    val code: String,
    val groupId: GroupId,
    val expiresAt: Long,
)
