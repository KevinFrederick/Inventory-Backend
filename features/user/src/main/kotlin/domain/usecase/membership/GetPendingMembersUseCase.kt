package domain.usecase.membership

import domain.model.PendingMember
import domain.repository.GroupMembershipRepository
import model.AppRole
import model.GroupId
import model.UserId
import repository.UserRoleProvider
import result.DomainResult
import result.ErrorType

class GetPendingMembersUseCase(
    private val membershipRepository: GroupMembershipRepository,
    private val userRoleProvider: UserRoleProvider
) {
    suspend operator fun invoke(
        requesterId: UserId,
        groupId: GroupId
    ): DomainResult<List<PendingMember>> {
        val role = userRoleProvider.getUserRole(requesterId, groupId)
        if (role != AppRole.OWNER && role != AppRole.ADMIN) {
            return DomainResult.Error("Only Admins and Owners can view pending requests.", ErrorType.FORBIDDEN)
        }

        return membershipRepository.getPendingMembers(groupId)
    }
}