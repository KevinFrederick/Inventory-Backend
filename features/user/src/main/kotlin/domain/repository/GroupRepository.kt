package domain.repository

import model.Group
import model.AppRole
import model.GroupId
import domain.model.GroupMember
import model.GroupWithRole
import domain.model.UserGroup
import model.UserId
import result.DomainResult

interface GroupRepository {
    suspend fun getGroupById(groupId: GroupId): DomainResult<Group?>
    suspend fun createGroup(group: Group, owner: UserGroup): DomainResult<GroupWithRole>
    suspend fun updateGroup(group: Group): DomainResult<Group>
    suspend fun deleteGroup(groupId: GroupId): DomainResult<Unit>

    suspend fun addUserToGroup(userGroup: UserGroup): DomainResult<GroupMember>
    suspend fun getGroupsForUser(userId: UserId): DomainResult<List<GroupWithRole>>
    suspend fun getGroupMembers(groupId: GroupId): DomainResult<List<GroupMember>>
    suspend fun updateMemberRole(userId: UserId, groupId: GroupId, newRole: AppRole): DomainResult<GroupMember>
    suspend fun removeMember(userId: UserId, groupId: GroupId): DomainResult<Unit>
}