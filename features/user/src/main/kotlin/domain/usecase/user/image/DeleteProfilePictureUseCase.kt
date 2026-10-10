package domain.usecase.user.image

import domain.ImageStorageService
import domain.repository.UserRepository
import model.UserId
import result.DomainResult
import result.ErrorType

class DeleteProfilePictureUseCase (
    private val imageStorageService: ImageStorageService,
    private val userRepository: UserRepository
)  {
    suspend operator fun invoke(
        userId: UserId,
    ): DomainResult<Unit> {
        val imageResult = when(
            val userResult = userRepository.getUserById(userId)
        ) {
            is DomainResult.Success -> {
                val user = userResult.data
                    ?: return DomainResult.Error("User not found", ErrorType.NOT_FOUND)

                user.avatarUrl
            }
            is DomainResult.Error -> return userResult
        }

        if (imageResult == null) return DomainResult.Success(Unit)


        return when(
            val storageResult = imageStorageService.deleteImage(imageResult)
        ) {
            is DomainResult.Success -> {
                userRepository.deleteUserProfilePicture(
                    userId = userId,
                )
            }
            is DomainResult.Error -> storageResult
        }
    }
}