package domain.usecase.membership

import domain.repository.GroupMembershipRepository
import domain.repository.InviteRepository
import model.UserId
import result.DomainResult
import result.ErrorType

class JoinGroupWithCodeUseCase(
    private val inviteRepository: InviteRepository,
    private val membershipRepository: GroupMembershipRepository
) {
    suspend operator fun invoke(
        userId: UserId,
        code: String
    ): DomainResult<Unit> {
        val invite = when (
            val inviteResult = inviteRepository.getInviteByCode(code.uppercase())
        ) {
            is DomainResult.Success -> inviteResult.data
            is DomainResult.Error -> return DomainResult.Error(inviteResult.message, inviteResult.errorType)
        }

        if (invite == null) {
            return DomainResult.Error("Invalid invite code.", ErrorType.NOT_FOUND)
        }

        if (System.currentTimeMillis() > invite.expiresAt) {
            inviteRepository.deleteInviteCode(code)
            return DomainResult.Error("This invite code has expired.", ErrorType.BAD_REQUEST)
        }

        return membershipRepository.addPendingMember(userId, invite.groupId)
    }
}