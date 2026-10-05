package domain.usecase.user_group

import domain.model.Group
import domain.repository.GroupRepository
import model.UserId
import result.DomainResult

class GetGroupsForUserUseCase (
    val groupRepository: GroupRepository
) {
    suspend operator fun invoke(userId: UserId): DomainResult<List<Group>> =
        groupRepository.getGroupsForUser(userId)
}