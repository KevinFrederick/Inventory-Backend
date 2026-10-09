package domain.model

import model.GroupId

@JvmInline
value class CategoryId(val value: String)

data class Category(
    val categoryId: CategoryId,
    val groupId: GroupId,
    val name: String,
    val description: String?,
    val createdAt: Long,
    val lastUpdated: Long
)
