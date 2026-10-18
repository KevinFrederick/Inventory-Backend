package domain.usecase.user_group

import model.GroupWithRole
import model.UserId
import repository.SharedGroupProvider
import result.DomainResult

class GetGroupsForUserUseCase (
    val groupProvider: SharedGroupProvider
) {
    suspend operator fun invoke(userId: UserId): DomainResult<List<GroupWithRole>> =
        groupProvider.getGroupsForUser(userId)
}