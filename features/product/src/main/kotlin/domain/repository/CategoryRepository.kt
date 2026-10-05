package domain.repository

import domain.model.Category
import domain.model.CategoryId
import model.GroupId
import result.DomainResult

interface CategoryRepository {
    suspend fun getAllCategory(groupId: GroupId): DomainResult<List<Category>>
    suspend fun getCategoryById(categoryId: CategoryId, groupId: GroupId): DomainResult<Category>
    suspend fun insertCategory(category: Category, groupId: GroupId): DomainResult<Unit>
    suspend fun updateCategory(category: Category, groupId: GroupId): DomainResult<Unit>
    suspend fun deleteCategory(categoryId: CategoryId, groupId: GroupId): DomainResult<Unit>
}