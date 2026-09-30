package domain.model

data class UserGroup(
    val userId: UserId,
    val groupId: GroupId,
    val role: AppRole,
    val joinedAt: Long,
)
