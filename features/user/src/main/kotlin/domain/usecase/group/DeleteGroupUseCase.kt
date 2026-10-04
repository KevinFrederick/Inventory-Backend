package domain.usecase.group

import domain.model.AppRole
import domain.model.GroupId
import domain.repository.GroupRepository
import model.UserId
import result.DomainResult
import result.ErrorType

class DeleteGroupUseCase (
    private val groupRepository: GroupRepository,
) {
    suspend operator fun invoke(
        requesterId: UserId,
        groupId: GroupId
    ): DomainResult<Unit> {
        val role = groupRepository.getUserRoleInGroup(requesterId, groupId)
            ?: return DomainResult.Error("You are not a member of this group.", ErrorType.FORBIDDEN)

        if (role != AppRole.OWNER) {
            return DomainResult.Error("Only the group owner can delete the group.", ErrorType.FORBIDDEN)
        }

        return groupRepository.deleteGroup(groupId)
    }
}