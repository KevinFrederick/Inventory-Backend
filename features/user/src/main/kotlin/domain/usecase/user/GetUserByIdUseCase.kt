package domain.usecase.user

import domain.repository.UserRepository
import model.User
import model.UserId
import result.DomainResult
import result.ErrorType

class GetUserByIdUseCase (
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(userId: UserId): DomainResult<User> {
        return when(
            val result = userRepository.getUserById(userId)
        ) {
            is DomainResult.Success -> {
                val user = result.data

                if (user == null) {
                    DomainResult.Error("User not found", ErrorType.NOT_FOUND)
                } else {
                    DomainResult.Success(user)
                }
            }
            is DomainResult.Error -> result
        }
    }
}