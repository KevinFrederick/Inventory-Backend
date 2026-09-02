package domain.usecase.category

import domain.model.CategoryId
import domain.repository.CategoryRepository
import domain.result.DomainResult

class DeleteCategoryUseCase (
    private val repository: CategoryRepository,
) {
    suspend operator fun invoke(categoryId: CategoryId): DomainResult<Unit> =
        repository.deleteCategory(categoryId)
}