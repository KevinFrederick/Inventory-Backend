package domain.model.sync

import domain.model.CategoryId

data class SyncCategory(
    val categoryId: CategoryId,
    val name: String,
    val description: String? = null,
    val createdAt: Long,
    val lastUpdated: Long
)
