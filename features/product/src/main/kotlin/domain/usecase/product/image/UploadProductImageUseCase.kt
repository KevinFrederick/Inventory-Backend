package domain.usecase.product.image

import domain.ImageStorageService
import domain.model.ProductId
import domain.repository.ProductRepository
import domain.usecase.util.convertToJpgBytes
import model.GroupId
import result.DomainResult
import result.ErrorType

class UploadProductImageUseCase(
    private val imageStorageService: ImageStorageService,
    private val productRepository: ProductRepository
) {
    companion object {
        private const val MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024
    }

    suspend operator fun invoke(
        productId: ProductId,
        fileBytes: ByteArray,
        groupId: GroupId
    ): DomainResult<String> {
        if (fileBytes.size > MAX_FILE_SIZE_BYTES) {
            return DomainResult.Error(
                "Image size exceeds maximum file size limit",
                ErrorType.BAD_REQUEST
            )
        }

        val jpgBytes = convertToJpgBytes(fileBytes)
        val filename = "${productId.value}.jpg"

        val uploadResult = when(
            val result = imageStorageService.saveImage(jpgBytes, filename)
        ) {
            is DomainResult.Success -> result.data
            is DomainResult.Error -> return result
        }

        when(
            val result = productRepository.updateProductImage(
                productId = productId,
                imageUriPath = uploadResult,
                updatedAt = System.currentTimeMillis(),
                groupId = groupId
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