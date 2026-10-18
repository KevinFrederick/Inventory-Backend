package data.repository

import DatabaseFactory.dbQuery
import data.table.user.UserGroupTable
import data.table.user.UserTable
import domain.model.PendingMember
import domain.repository.GroupMembershipRepository
import model.AppRole
import model.GroupId
import model.MembershipStatus
import model.UserId
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import result.DomainResult
import result.ErrorType

class GroupMembershipRepositoryImpl: GroupMembershipRepository {
    override suspend fun addPendingMember(
        userId: UserId,
        groupId: GroupId
    ): DomainResult<Unit> = dbQuery {
        try {
            UserGroupTable.insert {
                it[this.userId] = userId.value
                it[this.groupId] = groupId.value
                it[this.role] = AppRole.MEMBER.name
                it[this.status] = MembershipStatus.PENDING.name
                it[this.joinedAt] = System.currentTimeMillis()
                it[this.serverUpdatedAt] = System.currentTimeMillis()
            }
            DomainResult.Success(Unit)
        } catch (_: ExposedSQLException) {
            DomainResult.Error("You have already requested to join or are already a member.", ErrorType.CONFLICT)
        } catch (_: Exception) {
            DomainResult.Error("Failed to request access", ErrorType.UNKNOWN)
        }
    }

    override suspend fun getPendingMembers(groupId: GroupId): DomainResult<List<PendingMember>> = dbQuery {
        try {
            val pendingUsers = (UserGroupTable innerJoin UserTable)
                .selectAll()
                .where {
                    (UserGroupTable.groupId eq groupId.value) and
                    (UserGroupTable.status eq MembershipStatus.PENDING.name)
                }
                .map {
                    PendingMember(
                        userId = UserId(it[UserTable.userId]),
                        email = it[UserTable.email],
                        name = it[UserTable.name],
                        requestedAt = it[UserGroupTable.joinedAt]
                    )
                }
            DomainResult.Success(pendingUsers)
        } catch (_: Exception) {
            DomainResult.Error("Failed to fetch pending members", ErrorType.UNKNOWN)
        }
    }

    override suspend fun approveMember(
        userId: UserId,
        groupId: GroupId,
        role: AppRole
    ): DomainResult<Unit> = dbQuery {
        try {
            val updatedRows = UserGroupTable.update({
                (UserGroupTable.userId eq userId.value) and
                        (UserGroupTable.groupId eq groupId.value)
            }) {
                it[this.status] = MembershipStatus.ACTIVE.name
                it[this.role] = role.name
                it[this.serverUpdatedAt] = System.currentTimeMillis()
            }

            if (updatedRows > 0) {
                DomainResult.Success(Unit)
            } else {
                DomainResult.Error("Membership request not found", ErrorType.NOT_FOUND)
            }
        } catch (_: Exception) {
            DomainResult.Error("Failed to approve member", ErrorType.UNKNOWN)
        }
    }

    override suspend fun removeMember(userId: UserId, groupId: GroupId): DomainResult<Unit> = dbQuery {
        try {
            UserGroupTable.deleteWhere {
                (this.userId eq userId.value) and (this.groupId eq groupId.value)
            }
            DomainResult.Success(Unit)
        } catch (_: Exception) {
            DomainResult.Error("Failed to remove/reject member", ErrorType.UNKNOWN)
        }
    }
}