package domain.usecase.category

import domain.model.Category
import domain.model.CategoryId
import domain.repository.CategoryRepository
import domain.validation.CategoryValidator
import result.DomainResult
import result.ErrorType

class UpdateCategoryUseCase (
    private val repository: CategoryRepository,
    private val categoryValidator: CategoryValidator
) {
    suspend operator fun invoke (
        categoryId: CategoryId,
        category: Category
    ): DomainResult<Unit> {
        return if (category.categoryId != categoryId) {
            DomainResult.Error("Category Id doesn't match", ErrorType.BAD_REQUEST)
        } else {
            when(
                val validationResult = categoryValidator.validateCategory(category)
            ) {
                is DomainResult.Error -> return validationResult
                is DomainResult.Success -> {}
            }

            repository.updateCategory(category)
        }
    }
}