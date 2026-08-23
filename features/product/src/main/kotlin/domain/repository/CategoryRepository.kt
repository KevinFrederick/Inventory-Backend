package domain.repository

import domain.model.Category
import domain.model.CategoryId

interface CategoryRepository {
    suspend fun getAllCategory(): List<Category>
    suspend fun getCategoryById(categoryId: CategoryId): Category?
    suspend fun insertCategory(category: Category): Boolean
    suspend fun updateCategory(category: Category): Boolean
    suspend fun deleteCategory(categoryId: CategoryId): Boolean
}