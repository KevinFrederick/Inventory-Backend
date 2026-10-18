package domain.usecase.membership

import domain.repository.GroupMembershipRepository
import model.AppRole
import model.GroupId
import model.UserId
import repository.UserRoleProvider
import result.DomainResult
import result.ErrorType

class RejectPendingMemberUseCase(
    private val membershipRepository: GroupMembershipRepository,
    private val userRoleProvider: UserRoleProvider
) {
    suspend operator fun invoke(
        requesterId: UserId,
        groupId: GroupId,
        targetUserId: UserId
    ): DomainResult<Unit> {
        val role = userRoleProvider.getUserRole(requesterId, groupId)
        if (role != AppRole.OWNER && role != AppRole.ADMIN) {
            return DomainResult.Error("Only Admins and Owners can reject requests.", ErrorType.FORBIDDEN)
        }

        return membershipRepository.removeMember(targetUserId, groupId)
    }
}