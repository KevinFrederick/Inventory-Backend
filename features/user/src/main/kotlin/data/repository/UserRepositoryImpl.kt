package data.repository

import DatabaseFactory.dbQuery
import data.table.user.UserTable
import domain.repository.UserRepository
import mapper.toDomain
import model.User
import model.UserId
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import result.DomainResult
import result.ErrorType

class UserRepositoryImpl: UserRepository {
    override suspend fun getUserById(userId: UserId): DomainResult<User?> = dbQuery {
        try {
            val user = UserTable.selectAll()
                .where { UserTable.userId eq userId.value }
                .map { it.toDomain() }
                .singleOrNull()

            DomainResult.Success(user)
        } catch (e: Exception) {
            DomainResult.Error(e.localizedMessage ?: "Unknown Error", ErrorType.UNKNOWN)
        }
    }

    override suspend fun getUserByEmail(email: String): User? = dbQuery {
        UserTable.selectAll()
            .where { UserTable.email eq email }
            .map { it.toDomain() }
            .singleOrNull()
    }

    override suspend fun updateUser(user: User): DomainResult<User> = dbQuery {
        try {
            val updatedRows = UserTable.update ({ UserTable.userId eq user.userId.value }) {
                it[name] = user.name
                it[avatarUrl] = user.avatarUrl
                it[phoneNumber] = user.phoneNumber
                it[jobTitle] = user.jobTitle
                it[locale] = user.locale
                it[timeZone] = user.timeZone
                it[createdAt] = user.createdAt
                it[lastUpdated] = user.lastUpdated
                it[serverUpdatedAt] = System.currentTimeMillis()
            }

            if (updatedRows > 0) {
                DomainResult.Success(user)
            } else {
                DomainResult.Error("User not found", ErrorType.NOT_FOUND)
            }
        } catch (e: Exception) {
            DomainResult.Error(e.localizedMessage ?: "Unknown Error", ErrorType.UNKNOWN)
        }
    }

    override suspend fun deleteUser(userId: UserId): DomainResult<Unit> = dbQuery {
        try {
            val deletedRows = UserTable.deleteWhere { UserTable.userId eq userId.value }

            if (deletedRows > 0) {
                DomainResult.Success(Unit)
            } else {
                DomainResult.Error("User not found", ErrorType.NOT_FOUND)
            }
        } catch (e: Exception) {
            DomainResult.Error(e.localizedMessage ?: "Unknown Error", ErrorType.UNKNOWN)
        }
    }
}