package domain.usecase.category

import domain.model.Category
import domain.model.CategoryId
import domain.repository.CategoryRepository
import domain.result.DomainResult
import domain.result.ErrorType

class GetCategoryByIdUseCase (
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(categoryId: CategoryId): DomainResult<Category> {
        return when (
            val result = repository.getCategoryById(categoryId)
        ) {
            is DomainResult.Success -> {
                if (result.data == null) {
                    DomainResult.Error("Category Not Found", ErrorType.NOT_FOUND)
                }
                else {
                    DomainResult.Success(result.data)
                }
            }
            is DomainResult.Error -> result
        }
    }
}