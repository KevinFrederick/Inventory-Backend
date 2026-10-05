package domain.model

import model.GroupId

data class Group(
    val groupId: GroupId,
    val name: String,
    val description: String?,
    val address: String?,
    val createdAt: Long,
    val lastUpdated: Long
)
