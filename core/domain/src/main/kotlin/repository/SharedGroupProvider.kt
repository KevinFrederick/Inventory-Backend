package repository

import model.GroupWithRole
import model.UserId
import result.DomainResult

interface SharedGroupProvider {
    suspend fun getGroupsForUser(userId: UserId): DomainResult<List<GroupWithRole>>
}