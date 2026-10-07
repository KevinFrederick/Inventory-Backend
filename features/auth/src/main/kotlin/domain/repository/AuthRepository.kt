package domain.repository

import domain.model.RefreshToken
import model.Group
import model.User
import model.UserId
import result.DomainResult

interface AuthRepository {
    suspend fun registerUser(user: User, group: Group): DomainResult<User>
    suspend fun findUserByEmail(email: String): User?

    suspend fun saveRefreshToken(userId: UserId, token: String, expiresAt: Long)
    suspend fun findRefreshToken(token: String): RefreshToken?
    suspend fun revokeRefreshToken(token: String)
    suspend fun revokeAllUserTokens(userId: UserId)
    suspend fun deleteExpiredTokens()
}