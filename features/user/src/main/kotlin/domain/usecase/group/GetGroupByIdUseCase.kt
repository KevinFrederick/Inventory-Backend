package domain.usecase.group

import domain.model.GroupWithRole
import model.GroupId
import domain.repository.GroupRepository
import model.UserId
import repository.UserRoleProvider
import result.DomainResult
import result.ErrorType

class GetGroupByIdUseCase (
    private val groupRepository: GroupRepository,
    private val userRoleProvider: UserRoleProvider
) {
    suspend operator fun invoke(
        requesterId: UserId,
        groupId: GroupId
    ): DomainResult<GroupWithRole> {
        val role = userRoleProvider.getUserRole(requesterId, groupId)
            ?: return DomainResult.Error("You do not have permission to access this group", ErrorType.FORBIDDEN)

        return when(
            val result = groupRepository.getGroupById(groupId)
        ) {
            is DomainResult.Success -> {
                val group = result.data

                if (group == null) {
                    DomainResult.Error("Group not found", ErrorType.NOT_FOUND)
                } else {
                    DomainResult.Success(
                        GroupWithRole(
                            group = group,
                            role = role
                        )
                    )
                }
            }
            is DomainResult.Error -> result
        }
    }
}