package domain.usecase.user_group

import domain.model.AppRole
import domain.model.GroupId
import domain.repository.GroupRepository
import model.UserId
import result.DomainResult
import result.ErrorType

class UpdateMemberRoleUseCase (
    private val groupRepository: GroupRepository,
) {
    suspend operator fun invoke(
        requesterId: UserId,
        targetUserId: UserId,
        groupId: GroupId,
        newRole: AppRole
    ): DomainResult<Unit> {
        val requesterRole = groupRepository.getUserRoleInGroup(requesterId, groupId)
            ?: return DomainResult.Error("You do not have permission to access this group", ErrorType.FORBIDDEN)

        if (requesterRole != AppRole.OWNER) {
            return DomainResult.Error("Only owner can update member roles", ErrorType.FORBIDDEN)
        }

        if (requesterId == targetUserId && newRole != AppRole.OWNER) {
            return DomainResult.Error("You cannot demote yourself. Transfer ownership instead.", ErrorType.FORBIDDEN)
        }

        groupRepository.getUserRoleInGroup(targetUserId, groupId)
            ?: return DomainResult.Error("Target user is not a member of this group", ErrorType.FORBIDDEN)

        return groupRepository.updateMemberRole(targetUserId, groupId, newRole)
    }
}