package domain.usecase.user.image

import domain.ImageStorageService
import domain.repository.UserRepository
import model.UserId
import result.DomainResult
import result.ErrorType
import util.convertToJpgBytes

class UploadProfilePictureUseCase (
    private val imageStorageService: ImageStorageService,
    private val userRepository: UserRepository
) {
    companion object {
        private const val MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024
    }

    suspend operator fun invoke(
        userId: UserId,
        fileBytes: ByteArray
    ): DomainResult<String> {
        if (fileBytes.size > MAX_FILE_SIZE_BYTES) {
            return DomainResult.Error(
                "Image size exceeds maximum file size limit",
                ErrorType.BAD_REQUEST
            )
        }

        val jpgBytes = convertToJpgBytes(fileBytes)
        val filename = "${userId.value}.jpg"

        val uploadResult = when(
            val result = imageStorageService.saveImage(jpgBytes, filename)
        ) {
            is DomainResult.Success -> result.data
            is DomainResult.Error -> return result
        }

        when(
            val result = userRepository.updateUserProfile(
                userId = userId,
                imagePath = uploadResult
            )
        ) {
            is DomainResult.Success -> Unit
            is DomainResult.Error -> {
                imageStorageService.deleteImage(uploadResult)
                return result
            }
        }

        return DomainResult.Success(uploadResult)
    }
}