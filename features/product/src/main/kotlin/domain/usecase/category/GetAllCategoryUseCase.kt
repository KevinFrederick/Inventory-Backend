package domain.usecase.category

import domain.model.Category
import model.GroupId
import domain.repository.CategoryRepository
import result.DomainResult

class GetAllCategoryUseCase (
    private val repository: CategoryRepository,
) {
    suspend operator fun invoke(
        groupId: GroupId)
    : DomainResult<List<Category>> =
        repository.getAllCategory(groupId)
}