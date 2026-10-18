package data.repository

import DatabaseFactory.dbQuery
import data.table.user.GroupInviteTable
import domain.model.InviteDetail
import domain.repository.InviteRepository
import model.GroupId
import model.UserId
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import result.DomainResult
import result.ErrorType

class InviteRepositoryImpl: InviteRepository {
    override suspend fun createInviteCode(
        groupId: GroupId,
        createdBy: UserId,
        code: String,
        expiresAt: Long
    ): DomainResult<String> = dbQuery {
        try {
            GroupInviteTable.insert {
                it[this.code] = code
                it[this.groupId] = groupId.value
                it[this.createdBy] = createdBy.value
                it[this.expiresAt] = expiresAt
                it[this.createdAt] = System.currentTimeMillis()
            }
            DomainResult.Success(code)
        } catch (_: Exception) {
            DomainResult.Error("Failed to create invite code", ErrorType.UNKNOWN)
        }
    }

    override suspend fun getInviteByCode(code: String): DomainResult<InviteDetail?> = dbQuery {
        try {
            val row = GroupInviteTable
                .selectAll()
                .where { GroupInviteTable.code eq code }
                .singleOrNull()

            if (row == null) {
                DomainResult.Success(null)
            } else {
                DomainResult.Success(
                    InviteDetail(
                        code = row[GroupInviteTable.code],
                        groupId = GroupId(row[GroupInviteTable.groupId]),
                        expiresAt = row[GroupInviteTable.expiresAt]
                    )
                )
            }
        } catch (_: Exception) {
            DomainResult.Error("Failed to fetch invite", ErrorType.UNKNOWN)
        }
    }

    override suspend fun deleteInviteCode(code: String): DomainResult<Unit> = dbQuery {
        try {
            val deletedRows = GroupInviteTable.deleteWhere {
                GroupInviteTable.code eq code
            }

            if (deletedRows > 0) {
                DomainResult.Success(Unit)
            } else {
                DomainResult.Error("Invite code not found", ErrorType.NOT_FOUND)
            }
        } catch (_: Exception) {
            DomainResult.Error("Failed to delete invite code", ErrorType.UNKNOWN)
        }
    }
}