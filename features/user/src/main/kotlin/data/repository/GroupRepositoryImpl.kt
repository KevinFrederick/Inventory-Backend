package data.repository

import DatabaseFactory.dbQuery
import data.mapper.toDomain
import data.table.user.GroupTable
import data.table.user.UserGroupTable
import data.table.user.UserTable
import domain.model.Group
import model.AppRole
import model.GroupId
import domain.model.GroupMember
import domain.model.GroupWithRole
import domain.model.UserGroup
import domain.repository.GroupRepository
import model.UserId
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import org.slf4j.LoggerFactory
import repository.UserRoleProvider
import result.DomainResult
import result.ErrorType

class GroupRepositoryImpl: GroupRepository, UserRoleProvider {
    private val logger = LoggerFactory.getLogger(GroupRepositoryImpl::class.java)

    override suspend fun getGroupById(groupId: GroupId): DomainResult<Group?> = dbQuery {
        try {
            val group = GroupTable.selectAll()
                .where { GroupTable.groupId eq groupId.value }
                .map { it.toDomain() }
                .singleOrNull()

            DomainResult.Success(group)
        } catch (e: Exception) {
            DomainResult.Error(e.localizedMessage ?: "Unknown Error", ErrorType.UNKNOWN)
        }
    }

    override suspend fun createGroup(group: Group, owner: UserGroup): DomainResult<GroupWithRole> = dbQuery {
        try {
            val timestamp = System.currentTimeMillis()

            transaction {
                GroupTable.insert {
                    it[groupId] = group.groupId.value
                    it[name] = group.name
                    it[description] = group.description
                    it[address] = group.address
                    it[createdAt] = group.createdAt
                    it[lastUpdated] = group.lastUpdated
                    it[serverUpdatedAt] = timestamp
                }

                UserGroupTable.insert {
                    it[userId] = owner.userId.value
                    it[groupId] = group.groupId.value
                    it[role] = owner.role.name
                    it[joinedAt] = owner.joinedAt
                    it[serverUpdatedAt] = timestamp
                }
            }

            DomainResult.Success(
                GroupWithRole(
                    group = group,
                    role = owner.role
                )
            )
        } catch (e: ExposedSQLException) {
            logger.error("Exposed constraint violation for Group ${group.groupId.value}", e)
            DomainResult.Error("Failed to create group due to database constraint.", ErrorType.CONFLICT)
        } catch (e: Exception) {
            DomainResult.Error(e.localizedMessage ?: "Failed to create group", ErrorType.UNKNOWN)
        }
    }

    override suspend fun updateGroup(group: Group): DomainResult<Group> = dbQuery {
        try {
            val updatedRows = GroupTable.update({ GroupTable.groupId eq group.groupId.value }) {
                it[name] = group.name
                it[description] = group.description
                it[address] = group.address
                it[createdAt] = group.createdAt
                it[lastUpdated] = group.lastUpdated
                it[serverUpdatedAt] = System.currentTimeMillis()
            }

            if (updatedRows > 0) {
                DomainResult.Success(group)
            } else {
                DomainResult.Error("Group not found.", ErrorType.NOT_FOUND)
            }
        } catch (e: Exception) {
            DomainResult.Error(e.localizedMessage ?: "Unknown Error", ErrorType.UNKNOWN)
        }
    }

    override suspend fun deleteGroup(groupId: GroupId): DomainResult<Unit> = dbQuery {
        try {
            val deletedRows = GroupTable.deleteWhere { GroupTable.groupId eq groupId.value }

            if (deletedRows > 0) {
                DomainResult.Success(Unit)
            } else {
                DomainResult.Error("Group not found.", ErrorType.NOT_FOUND)
            }
        } catch (e: Exception) {
            DomainResult.Error(e.localizedMessage ?: "Unknown Error", ErrorType.UNKNOWN)
        }
    }

    override suspend fun addUserToGroup(userGroup: UserGroup): DomainResult<GroupMember> = dbQuery {
        try {
            UserGroupTable.insert {
                it[userId] = userGroup.userId.value
                it[groupId] = userGroup.groupId.value
                it[role] = userGroup.role.name
                it[joinedAt] = userGroup.joinedAt
                it[serverUpdatedAt] = System.currentTimeMillis()
            }

            val memberResult = (UserTable innerJoin UserGroupTable).selectAll()
                .where {
                    (UserGroupTable.userId eq userGroup.userId.value) and
                    (UserGroupTable.groupId eq userGroup.groupId.value)
                }
                .single()

            val groupMember = GroupMember(
                userId = UserId(memberResult[UserTable.userId]),
                avatarUrl = memberResult[UserTable.avatarUrl],
                name = memberResult[UserTable.name],
                email = memberResult[UserTable.email],
                role = AppRole.valueOf(memberResult[UserGroupTable.role]),
                joinedAt = memberResult[UserGroupTable.joinedAt]
            )

            DomainResult.Success(groupMember)
        } catch (_: ExposedSQLException) {
            DomainResult.Error("User is already in this group.", ErrorType.CONFLICT)
        } catch (e: Exception) {
            DomainResult.Error(e.localizedMessage ?: "Failed to add user", ErrorType.UNKNOWN)
        }
    }

    override suspend fun getGroupsForUser(userId: UserId): DomainResult<List<GroupWithRole>> = dbQuery {
        try {
            val groups = (GroupTable innerJoin UserGroupTable).selectAll()
                .where { UserGroupTable.userId eq userId.value }
                .map { GroupWithRole(
                    group = it.toDomain(),
                    role = AppRole.valueOf(it[UserGroupTable.role])
                ) }

            DomainResult.Success(groups)
        } catch (e: Exception) {
            DomainResult.Error(e.localizedMessage ?: "Failed to get groups", ErrorType.UNKNOWN)
        }
    }

    override suspend fun getGroupMembers(groupId: GroupId): DomainResult<List<GroupMember>> = dbQuery {
        try {
            val members = (UserTable innerJoin UserGroupTable).selectAll()
                .where { UserGroupTable.groupId eq groupId.value }
                .map {
                    GroupMember(
                        userId = UserId(it[UserTable.userId]),
                        avatarUrl = it[UserTable.avatarUrl],
                        name = it[UserTable.name],
                        email = it[UserTable.email],
                        role = AppRole.valueOf(it[UserGroupTable.role]),
                        joinedAt = it[UserGroupTable.joinedAt]
                    )
                }

            DomainResult.Success(members)
        } catch (e: Exception) {
            DomainResult.Error(e.localizedMessage ?: "Failed to get group members", ErrorType.UNKNOWN)
        }
    }

    override suspend fun updateMemberRole(
        userId: UserId,
        groupId: GroupId,
        newRole: AppRole
    ): DomainResult<GroupMember> = dbQuery {
        try {
            val updatedRows = UserGroupTable.update({
                (UserGroupTable.userId eq userId.value) and (UserGroupTable.groupId eq groupId.value)
            }) {
                it[role] = newRole.name
                it[serverUpdatedAt] = System.currentTimeMillis()
            }

            if (updatedRows == 0) {
                return@dbQuery DomainResult.Error("Member not found.", ErrorType.NOT_FOUND)
            }

            val memberResult = (UserTable innerJoin UserGroupTable).selectAll()
                .where {
                    (UserTable.userId eq userId.value) and (UserGroupTable.groupId eq groupId.value)
                }
                .single()

            val groupMember = GroupMember(
                userId = UserId(memberResult[UserTable.userId]),
                avatarUrl = memberResult[UserTable.avatarUrl],
                name = memberResult[UserTable.name],
                email = memberResult[UserTable.email],
                role = AppRole.valueOf(memberResult[UserGroupTable.role]),
                joinedAt = memberResult[UserGroupTable.joinedAt]
            )

            if (updatedRows > 0) {
                DomainResult.Success(groupMember)
            } else {
                DomainResult.Error("Member not found.", ErrorType.NOT_FOUND)
            }
        } catch (e: Exception) {
            DomainResult.Error(e.localizedMessage ?: "Unknown Error", ErrorType.UNKNOWN)
        }
    }

    override suspend fun removeMember(
        userId: UserId,
        groupId: GroupId
    ): DomainResult<Unit> = dbQuery {
        try {
            val deletedRows = UserGroupTable.deleteWhere {
                (UserGroupTable.userId eq userId.value) and (UserGroupTable.groupId eq groupId.value)
            }

            if (deletedRows > 0) {
                DomainResult.Success(Unit)
            } else {
                DomainResult.Error("Member not found.", ErrorType.NOT_FOUND)
            }
        } catch (e: Exception) {
            DomainResult.Error(e.localizedMessage ?: "Unknown Error", ErrorType.UNKNOWN)
        }
    }

    override suspend fun getUserRole(
        userId: UserId,
        groupId: GroupId
    ): AppRole? = dbQuery {
        UserGroupTable
            .select(UserGroupTable.role)
            .where { (UserGroupTable.userId eq userId.value) and (UserGroupTable.groupId eq groupId.value) }
            .map { AppRole.valueOf(it[UserGroupTable.role]) }
            .singleOrNull()
    }
}