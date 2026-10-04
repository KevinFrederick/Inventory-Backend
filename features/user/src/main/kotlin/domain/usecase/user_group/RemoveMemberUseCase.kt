package domain.usecase.user_group

import domain.model.AppRole
import domain.model.GroupId
import domain.repository.GroupRepository
import model.UserId
import result.DomainResult
import result.ErrorType

class RemoveMemberUseCase (
    private val groupRepository: GroupRepository,
) {
    suspend operator fun invoke(
        requesterId: UserId,
        targetUserId: UserId,
        groupId: GroupId,
    ): DomainResult<Unit> {

        // Leaving
        if (requesterId == targetUserId) {
            val userRole = groupRepository.getUserRoleInGroup(requesterId, groupId)
                ?: return DomainResult.Error("You are not a member of this group.", ErrorType.NOT_FOUND)

            if (userRole == AppRole.OWNER) {
                return DomainResult.Error("The owner cannot leave. Delete the group or transfer ownership first.", ErrorType.FORBIDDEN)
            }

            return groupRepository.removeMember(targetUserId, groupId)
        }

        // Removing
        val requesterRole = groupRepository.getUserRoleInGroup(requesterId, groupId)
            ?: return DomainResult.Error("You are not a member of this group.", ErrorType.FORBIDDEN)

        if (requesterRole == AppRole.MEMBER) {
            return DomainResult.Error("Only owner and admins can remove members", ErrorType.FORBIDDEN)
        }

        val targetRole = groupRepository.getUserRoleInGroup(targetUserId, groupId)
            ?: return DomainResult.Error("Target user not a member of this group.", ErrorType.NOT_FOUND)

        if (requesterRole == AppRole.ADMIN && (targetRole == AppRole.OWNER || targetRole == AppRole.ADMIN)) {
            return DomainResult.Error("Admin can only remove standard members", ErrorType.FORBIDDEN)
        }

        return groupRepository.removeMember(targetUserId, groupId)
    }
}