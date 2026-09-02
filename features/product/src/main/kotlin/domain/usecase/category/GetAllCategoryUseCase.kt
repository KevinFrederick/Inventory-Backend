package domain.usecase.category

import domain.model.Category
import domain.repository.CategoryRepository
import domain.result.DomainResult

class GetAllCategoryUseCase (
    private val repository: CategoryRepository,
) {
    suspend operator fun invoke(): DomainResult<List<Category>> =
        repository.getAllCategory()
}