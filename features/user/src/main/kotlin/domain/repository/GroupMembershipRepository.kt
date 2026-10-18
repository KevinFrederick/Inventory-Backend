package domain.repository

import domain.model.PendingMember
import model.AppRole
import model.GroupId
import model.UserId
import result.DomainResult

interface GroupMembershipRepository {
    suspend fun addPendingMember(userId: UserId, groupId: GroupId): DomainResult<Unit>
    suspend fun getPendingMembers(groupId: GroupId): DomainResult<List<PendingMember>>
    suspend fun approveMember(userId: UserId, groupId: GroupId, role: AppRole): DomainResult<Unit>
    suspend fun removeMember(userId: UserId, groupId: GroupId): DomainResult<Unit>
}