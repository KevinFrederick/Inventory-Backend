package domain.usecase.category

import domain.model.Category
import domain.model.CategoryId
import model.GroupId
import domain.repository.CategoryRepository
import result.DomainResult

class GetCategoryByIdUseCase (
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(
        categoryId: CategoryId,
        groupId: GroupId
    ): DomainResult<Category> =
        repository.getCategoryById(categoryId, groupId)
}