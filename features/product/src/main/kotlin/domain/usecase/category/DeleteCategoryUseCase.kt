package domain.usecase.category

import domain.model.CategoryId
import domain.repository.CategoryRepository
import model.GroupId
import result.DomainResult

class DeleteCategoryUseCase (
    private val repository: CategoryRepository,
) {
    suspend operator fun invoke(
        categoryId: CategoryId,
        groupId: GroupId
    ): DomainResult<Unit> =
        repository.deleteCategory(categoryId, groupId)
}