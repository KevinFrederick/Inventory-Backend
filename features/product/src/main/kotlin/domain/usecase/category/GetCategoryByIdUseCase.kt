package domain.usecase.category

import domain.model.Category
import domain.model.CategoryId
import domain.repository.CategoryRepository
import result.DomainResult
import result.ErrorType

class GetCategoryByIdUseCase (
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(categoryId: CategoryId): DomainResult<Category> {
        return when (
            val result = repository.getCategoryById(categoryId)
        ) {
            is DomainResult.Success -> {
                val category = result.data

                if (category == null) {
                    DomainResult.Error("Category Not Found", ErrorType.NOT_FOUND)
                }
                else {
                    DomainResult.Success(category)
                }
            }
            is DomainResult.Error -> result
        }
    }
}