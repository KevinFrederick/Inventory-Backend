package domain.usecase.user_group

import model.GroupId
import domain.model.GroupMember
import domain.repository.GroupRepository
import model.UserId
import repository.UserRoleProvider
import result.DomainResult
import result.ErrorType

class GetGroupMembersUseCase (
    private val groupRepository: GroupRepository,
    private val userRoleProvider: UserRoleProvider
) {
    suspend operator fun invoke(
        requesterId: UserId,
        groupId: GroupId
    ): DomainResult<List<GroupMember>> {
        userRoleProvider.getUserRole(requesterId, groupId)
            ?: return DomainResult.Error("You do not have permission to access this group", ErrorType.FORBIDDEN)

        return groupRepository.getGroupMembers(groupId)
    }
}