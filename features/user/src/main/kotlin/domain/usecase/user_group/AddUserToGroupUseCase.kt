package domain.usecase.user_group

import domain.model.GroupMember
import model.AppRole
import model.GroupId
import domain.model.UserGroup
import domain.repository.GroupRepository
import domain.repository.UserRepository
import model.UserId
import repository.UserRoleProvider
import result.DomainResult
import result.ErrorType
import validation.ValidationResult
import validation.ValidationRules

class AddUserToGroupUseCase (
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val userRoleProvider: UserRoleProvider
) {
    suspend operator fun invoke(
        requesterId: UserId,
        groupId: GroupId,
        targetEmail: String,
        assignRole: AppRole
    ): DomainResult<GroupMember> {
        val emailErrors = ValidationRules.validateEmail(
            item = targetEmail,
            getId = {"Target Email"},
            getValue = { it }
        )

        if (emailErrors.isNotEmpty()) {
            ValidationResult.formatResult(emailErrors)
        }

        // Check Requester Permissions
        val requesterRole = userRoleProvider.getUserRole(requesterId, groupId)
            ?: return DomainResult.Error("User role not found", ErrorType.NOT_FOUND)

        if (requesterRole == AppRole.MEMBER) {
            return DomainResult.Error("Only admins or owners can add members", ErrorType.FORBIDDEN)
        }

        if (assignRole == AppRole.OWNER && requesterRole != AppRole.OWNER) {
            return DomainResult.Error("Only the current owner can grant owner role", ErrorType.FORBIDDEN)
        }

        val userResult = userRepository.getUserByEmail(targetEmail)
            ?: return DomainResult.Error("User not found", ErrorType.NOT_FOUND)

        val relation = UserGroup(
            userId = userResult.userId,
            groupId = groupId,
            role = assignRole,
            joinedAt = System.currentTimeMillis()
        )

        return groupRepository.addUserToGroup(relation)
    }
}