package repository

import model.AppRole
import model.GroupId
import model.UserId

interface UserRoleProvider {
    suspend fun getUserRole(userId: UserId, groupId: GroupId): AppRole?
}