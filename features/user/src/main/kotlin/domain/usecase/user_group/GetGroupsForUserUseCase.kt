package domain.usecase.user_group

import domain.model.GroupWithRole
import domain.repository.GroupRepository
import model.UserId
import result.DomainResult

class GetGroupsForUserUseCase (
    val groupRepository: GroupRepository
) {
    suspend operator fun invoke(userId: UserId): DomainResult<List<GroupWithRole>> =
        groupRepository.getGroupsForUser(userId)
}