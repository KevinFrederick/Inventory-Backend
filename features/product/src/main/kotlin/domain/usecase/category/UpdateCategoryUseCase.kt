package domain.usecase.category

import domain.model.Category
import domain.model.CategoryId
import domain.repository.CategoryRepository
import domain.result.DomainResult
import domain.result.ErrorType

class UpdateCategoryUseCase (
    private val repository: CategoryRepository
) {
    suspend operator fun invoke (
        categoryId: CategoryId,
        category: Category
    ): DomainResult<Unit> {
        return if (category.categoryId != categoryId) {
            DomainResult.Error("Category Id doesn't match", ErrorType.BAD_REQUEST)
        } else {
            repository.updateCategory(category)
        }
    }
}