package domain.repository

import domain.model.Category
import domain.model.CategoryId
import domain.result.DomainResult

interface CategoryRepository {
    suspend fun getAllCategory(): DomainResult<List<Category>>
    suspend fun getCategoryById(categoryId: CategoryId): DomainResult<Category?>
    suspend fun insertCategory(category: Category): DomainResult<Unit>
    suspend fun updateCategory(category: Category): DomainResult<Unit>
    suspend fun deleteCategory(categoryId: CategoryId): DomainResult<Unit>
}