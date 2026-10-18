package domain.repository

import domain.model.InviteDetail
import model.GroupId
import model.UserId
import result.DomainResult

interface InviteRepository {
    suspend fun createInviteCode(groupId: GroupId, createdBy: UserId, code: String, expiresAt: Long): DomainResult<String>
    suspend fun getInviteByCode(code: String): DomainResult<InviteDetail?>
    suspend fun deleteInviteCode(code: String): DomainResult<Unit>
}