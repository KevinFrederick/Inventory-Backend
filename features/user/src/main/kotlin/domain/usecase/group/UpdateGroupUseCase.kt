package domain.usecase.group

import domain.model.Group
import model.AppRole
import model.GroupId
import domain.repository.GroupRepository
import domain.validation.GroupValidator
import model.UserId
import repository.UserRoleProvider
import result.DomainResult
import result.ErrorType

class UpdateGroupUseCase (
    private val groupRepository: GroupRepository,
    private val groupValidator: GroupValidator,
    private val userRoleProvider: UserRoleProvider,
) {
    suspend operator fun invoke(
        requesterId: UserId,
        groupId: GroupId,
        name: String?,
        description: String?,
        address: String?,
    ): DomainResult<Group> {
        val role = userRoleProvider.getUserRole(requesterId, groupId)
            ?: return DomainResult.Error("You are not a member of this group.", ErrorType.FORBIDDEN)

        if (role == AppRole.MEMBER) {
            return DomainResult.Error("Only admins or the owner can update group details.", ErrorType.FORBIDDEN)
        }

        val existingGroup = when(
            val groupResult = groupRepository.getGroupById(groupId)
        ) {
            is DomainResult.Error -> return groupResult
            is DomainResult.Success -> groupResult.data
        }

        if (existingGroup == null) return DomainResult.Error("Group not found", ErrorType.NOT_FOUND)

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

        return groupRepository.updateGroup(updatedGroup)
    }
}