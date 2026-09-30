package domain.model

@JvmInline
value class GroupId(val value: String)

data class Group(
    val groupId: GroupId,
    val name: String,
    val description: String?,
    val createdAt: Long,
    val lastUpdated: Long
)
