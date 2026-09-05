package domain.model

@JvmInline
value class CategoryId(val value: String)

data class Category(
    val categoryId: CategoryId,
    val name: String,
    val description: String?,
    val createdAt: Long,
    val lastUpdated: Long
)
