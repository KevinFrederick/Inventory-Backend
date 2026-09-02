package domain.usecase.category

import domain.model.Category
import domain.repository.CategoryRepository
import domain.result.DomainResult

class InsertCategoryUseCase (
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category): DomainResult<Unit> =
        repository.insertCategory(category)
}