package domain.usecase.group

import model.GroupWithRole
import model.GroupId
import model.UserId
import repository.SharedGroupProvider
import result.DomainResult
import result.ErrorType

class GetGroupByIdUseCase (
    private val groupProvider: SharedGroupProvider,
    ) {
    suspend operator fun invoke(
        requesterId: UserId,
        groupId: GroupId
    ): DomainResult<GroupWithRole> {
        val userGroups = when (val result = groupProvider.getGroupsForUser(requesterId)) {
            is DomainResult.Success -> result.data
            is DomainResult.Error -> return DomainResult.Error("Failed to fetch membership", ErrorType.UNKNOWN)
        }

        val membership = userGroups.find { it.group.groupId == groupId }
            ?: return DomainResult.Error("You are not a member of this group.", ErrorType.FORBIDDEN)

        return DomainResult.Success(membership)
    }
}