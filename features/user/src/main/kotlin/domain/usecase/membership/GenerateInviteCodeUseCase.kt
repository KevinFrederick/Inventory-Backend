package domain.usecase.membership

import domain.repository.InviteRepository
import model.AppRole
import model.GroupId
import model.UserId
import repository.UserRoleProvider
import result.DomainResult
import result.ErrorType
import java.security.SecureRandom

class GenerateInviteCodeUseCase(
    private val inviteRepository: InviteRepository,
    private val userRoleProvider: UserRoleProvider // To check permissions
) {
    suspend operator fun invoke(
        requesterId: UserId,
        groupId: GroupId,
        validityDays: Int = 7
    ): DomainResult<String> {
        val role = userRoleProvider.getUserRole(requesterId, groupId)
        if (role != AppRole.OWNER && role != AppRole.ADMIN) {
            return DomainResult.Error("Only Admins and Owners can generate invites.", ErrorType.FORBIDDEN)
        }

        val allowedChars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray()
        val inviteCode = (1..8)
            .map { allowedChars[SecureRandom().nextInt(allowedChars.size)] }
            .joinToString("")

        val expiresAt = System.currentTimeMillis() + (validityDays * 24 * 60 * 60 * 1000L)

        return inviteRepository.createInviteCode(groupId, requesterId, inviteCode, expiresAt)
    }
}