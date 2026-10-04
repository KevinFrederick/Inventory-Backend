package domain.repository

import domain.model.AppRole
import domain.model.Group
import domain.model.GroupId
import domain.model.GroupMember
import domain.model.UserGroup
import model.UserId
import result.DomainResult

interface GroupRepository {
    suspend fun getGroupById(groupId: GroupId): DomainResult<Group?>
    suspend fun createGroup(group: Group, owner: UserGroup): DomainResult<Group>
    suspend fun updateGroup(group: Group): DomainResult<Group>
    suspend fun deleteGroup(groupId: GroupId): DomainResult<Unit>

    suspend fun addUserToGroup(userGroup: UserGroup): DomainResult<Unit>
    suspend fun getGroupsForUser(userId: UserId): DomainResult<List<Group>>
    suspend fun getGroupMembers(groupId: GroupId): DomainResult<List<GroupMember>>
    suspend fun getUserRoleInGroup(userId: UserId, groupId: GroupId): AppRole?
    suspend fun updateMemberRole(userId: UserId, groupId: GroupId, newRole: AppRole): DomainResult<Unit>
    suspend fun removeMember(userId: UserId, groupId: GroupId): DomainResult<Unit>
}