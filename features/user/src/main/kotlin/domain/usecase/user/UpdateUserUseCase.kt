package domain.usecase.user

import domain.repository.UserRepository
import domain.validation.UserValidator
import model.User
import model.UserId
import result.DomainResult
import result.ErrorType

class UpdateUserUseCase (
    private val userRepository: UserRepository,
    private val userValidator: UserValidator
) {
    suspend operator fun invoke(
        userId: UserId,
        name: String?,
        avatarUrl: String?,
        phoneNumber: String?,
        jobTitle: String?,
        locale: String?,
        timeZone: String?,
    ): DomainResult<User> {
        when(
            val userResult = userRepository.getUserById(userId)
        ) {
            is DomainResult.Error -> return userResult
            is DomainResult.Success -> {
                val existingUser = userResult.data
                    ?: return DomainResult.Error("User not found", ErrorType.NOT_FOUND)

                val updatedUser = existingUser.copy(
                    name = name?: existingUser.name,
                    avatarUrl = avatarUrl,
                    phoneNumber = phoneNumber,
                    jobTitle = jobTitle,
                    locale = locale,
                    timeZone = timeZone,
                    lastUpdated = System.currentTimeMillis(),
                )

                when(
                    val validationResult = userValidator.validateUser(updatedUser)
                ) {
                    is DomainResult.Error -> return validationResult
                    is DomainResult.Success -> {}
                }

                return userRepository.updateUser(updatedUser)
            }
        }
    }
}