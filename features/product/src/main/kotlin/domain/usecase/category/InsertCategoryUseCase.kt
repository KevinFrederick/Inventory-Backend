package domain.usecase.category

import domain.model.Category
import model.GroupId
import domain.repository.CategoryRepository
import domain.validation.CategoryValidator
import result.DomainResult

class InsertCategoryUseCase (
    private val repository: CategoryRepository,
    private val categoryValidator: CategoryValidator
) {
    suspend operator fun invoke(
        category: Category,
        groupId: GroupId
    ): DomainResult<Unit> {
        when(
            val validationResult = categoryValidator.validateCategory(category)
        ) {
            is DomainResult.Error -> return validationResult
            is DomainResult.Success -> {}
        }

        return repository.insertCategory(category, groupId)
    }
}