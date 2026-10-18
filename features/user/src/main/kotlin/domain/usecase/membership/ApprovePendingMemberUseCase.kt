package domain.usecase.membership

import domain.repository.GroupMembershipRepository
import model.AppRole
import model.GroupId
import model.UserId
import repository.UserRoleProvider
import result.DomainResult
import result.ErrorType

class ApprovePendingMemberUseCase(
    private val membershipRepository: GroupMembershipRepository,
    private val userRoleProvider: UserRoleProvider
) {
    suspend operator fun invoke(
        requesterId: UserId,
        groupId: GroupId,
        targetUserId: UserId,
        assignedRole: AppRole
    ): DomainResult<Unit> {
        val requesterRole = userRoleProvider.getUserRole(requesterId, groupId)
        if (requesterRole != AppRole.OWNER && requesterRole != AppRole.ADMIN) {
            return DomainResult.Error("Only Admins and Owners can approve members.", ErrorType.FORBIDDEN)
        }

        if (assignedRole == AppRole.OWNER && requesterRole != AppRole.OWNER) {
            return DomainResult.Error("Only the Owner can assign another Owner.", ErrorType.FORBIDDEN)
        }

        return membershipRepository.approveMember(targetUserId, groupId, assignedRole)
    }
}