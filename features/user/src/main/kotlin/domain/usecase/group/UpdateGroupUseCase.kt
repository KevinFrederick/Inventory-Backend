package domain.usecase.group

import model.GroupWithRole
import model.AppRole
import model.GroupId
import domain.repository.GroupRepository
import domain.validation.GroupValidator
import model.UserId
import repository.SharedGroupProvider
import result.DomainResult
import result.ErrorType

class UpdateGroupUseCase (
    private val groupRepository: GroupRepository,
    private val groupValidator: GroupValidator,
    private val groupProvider: SharedGroupProvider,
) {
    suspend operator fun invoke(
        requesterId: UserId,
        groupId: GroupId,
        name: String?,
        description: String?,
        address: String?,
    ): DomainResult<GroupWithRole> {
        val userGroups = when (val result = groupProvider.getGroupsForUser(requesterId)) {
            is DomainResult.Success -> result.data
            is DomainResult.Error -> return DomainResult.Error("Failed to fetch membership", ErrorType.UNKNOWN)
        }

        val membership = userGroups.find { it.group.groupId == groupId }
            ?: return DomainResult.Error("You are not a member of this group.", ErrorType.FORBIDDEN)

        if (membership.role == AppRole.MEMBER) {
            return DomainResult.Error("Only admins or the owner can update group details.", ErrorType.FORBIDDEN)
        }

        val existingGroup = membership.group

        val updatedGroup = existingGroup.copy(
            name = name ?: existingGroup.name,
            description = description,
            address = address,
            lastUpdated = System.currentTimeMillis()
        )

        when(
            val validationResult = groupValidator.validateGroup(updatedGroup)
        ) {
            is DomainResult.Error -> return validationResult
            is DomainResult.Success -> Unit
        }

        return when (
            val result = groupRepository.updateGroup(updatedGroup)
        ) {
            is DomainResult.Success -> {
                DomainResult.Success(
                    GroupWithRole(
                        group = result.data,
                        role = membership.role,
                        joinedAt = membership.joinedAt,
                    )
                )
            }
            is DomainResult.Error -> result
        }
    }
}