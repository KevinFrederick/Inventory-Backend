package domain.repository

import model.User
import model.UserId
import result.DomainResult

interface UserRepository {
    suspend fun getUserById(userId: UserId): DomainResult<User?>
    suspend fun getUserByEmail(email: String): User?
    suspend fun updateUser(user: User): DomainResult<User>
    suspend fun deleteUser(userId: UserId): DomainResult<Unit>
}