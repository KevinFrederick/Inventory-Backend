package domain.usecase.group

import model.AppRole
import model.GroupId
import domain.repository.GroupRepository
import model.UserId
import repository.UserRoleProvider
import result.DomainResult
import result.ErrorType

class DeleteGroupUseCase (
    private val groupRepository: GroupRepository,
    private val userRoleProvider: UserRoleProvider
) {
    suspend operator fun invoke(
        requesterId: UserId,
        groupId: GroupId
    ): DomainResult<Unit> {
        val role = userRoleProvider.getUserRole(requesterId, groupId)
            ?: return DomainResult.Error("You are not a member of this group.", ErrorType.FORBIDDEN)

        if (role != AppRole.OWNER) {
            return DomainResult.Error("Only the group owner can delete the group.", ErrorType.FORBIDDEN)
        }

        return groupRepository.deleteGroup(groupId)
    }
}