package domain.usecase.user

import domain.repository.UserRepository
import model.UserId
import result.DomainResult

class DeleteUserUseCase (
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(userId: UserId): DomainResult<Unit> =
        userRepository.deleteUser(userId)
}