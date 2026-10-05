package usecase

import model.AppRole
import model.GroupId
import model.UserId
import repository.UserRoleProvider
import result.DomainResult
import result.ErrorType

class VerifyUserGroupRoleUseCase(
    private val userRoleProvider: UserRoleProvider
) {
    suspend operator fun invoke(
        userId: UserId,
        groupId: GroupId
    ): DomainResult<AppRole> {
        if (userId.value.isEmpty() || groupId.value.isEmpty()) {
            return DomainResult.Error("Invalid user or group ID", ErrorType.BAD_REQUEST)
        }
        val userRole = userRoleProvider.getUserRole(
            userId = userId,
            groupId = groupId
        ) ?: return DomainResult.Error("You do not have access to this group", ErrorType.FORBIDDEN)

        return DomainResult.Success(userRole)
    }
}