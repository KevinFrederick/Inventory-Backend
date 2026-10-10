package data.repository

import DatabaseFactory.dbQuery
import mapper.toDomain
import data.mapper.toRefreshToken
import data.table.auth.RefreshTokenTable
import data.table.user.GroupTable
import data.table.user.UserGroupTable
import data.table.user.UserTable
import domain.model.RefreshToken
import model.User
import model.UserId
import domain.repository.AuthRepository
import model.AppRole
import model.Group
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import result.DomainResult
import result.ErrorType
import java.util.UUID

class AuthRepositoryImpl: AuthRepository {
    override suspend fun registerUser(user: User, group: Group): DomainResult<User> = dbQuery {
        try {
            val timeStamp = System.currentTimeMillis()

            UserTable.insert {
                it[userId] = user.userId.value
                it[email] = user.email
                it[passHash] = user.passHash
                it[name] = user.name
                it[avatarUrl] = user.avatarUrl
                it[phoneNumber] = user.phoneNumber
                it[jobTitle] = user.jobTitle
                it[locale] = user.locale
                it[timeZone] = user.timeZone
                it[isActive] = user.isActive
                it[createdAt] = user.createdAt
                it[lastUpdated] = user.lastUpdated
                it[serverUpdatedAt] = timeStamp
            }

            GroupTable.insert {
                it[groupId] = group.groupId.value
                it[name] = group.name
                it[description] = group.description
                it[address] = group.address
                it[createdAt] = group.createdAt
                it[lastUpdated] = group.lastUpdated
                it[serverUpdatedAt] = timeStamp
            }

            UserGroupTable.insert {
                it[userId] = user.userId.value
                it[groupId] = group.groupId.value
                it[role] = AppRole.OWNER.name
                it[joinedAt] = timeStamp
                it[serverUpdatedAt] = timeStamp
            }

            DomainResult.Success(user)
        } catch (e: ExposedSQLException) {
            val errorMessage = e.message ?: ""

            when {
                errorMessage.contains("users_email_unique") ->
                    DomainResult.Error("Email already exists", ErrorType.CONFLICT)
                else ->
                    DomainResult.Error("Failed to register user", ErrorType.UNKNOWN)
            }
        }
    }

    override suspend fun findUserByEmail(email: String): User? = dbQuery {
        UserTable
            .selectAll()
            .where { UserTable.email eq email }
            .singleOrNull()
            ?.toDomain()
    }

    override suspend fun saveRefreshToken(
        userId: UserId,
        token: String,
        expiresAt: Long
    ) = dbQuery {
        RefreshTokenTable.insert {
            it[id] = UUID.randomUUID().toString()
            it[RefreshTokenTable.userId] = userId.value
            it[RefreshTokenTable.token] = token
            it[RefreshTokenTable.expiresAt] = expiresAt
            it[isRevoked] = false
            it[createdAt] = System.currentTimeMillis()
        }
        Unit
    }

    override suspend fun findRefreshToken(token: String): RefreshToken? = dbQuery {
        RefreshTokenTable.selectAll()
            .where { RefreshTokenTable.token eq token }
            .mapNotNull { it.toRefreshToken() }
            .singleOrNull()
    }

    override suspend fun revokeRefreshToken(token: String) = dbQuery {
        RefreshTokenTable.update({ RefreshTokenTable.token eq token }) {
            it[isRevoked] = true
        }
        Unit
    }

    override suspend fun revokeAllUserTokens(userId: UserId) = dbQuery {
        RefreshTokenTable.update ({ RefreshTokenTable.userId eq userId.value }) {
            it[isRevoked] = true
        }
        Unit
    }

    override suspend fun deleteExpiredTokens() {
        dbQuery {
            RefreshTokenTable.deleteWhere {
                RefreshTokenTable.expiresAt lessEq System.currentTimeMillis()
            }
        }
    }
}