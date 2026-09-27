package domain.usecase.product.image

import domain.ImageStorageService
import domain.model.ProductId
import domain.usecase.util.convertToJpgBytes
import result.DomainResult
import result.ErrorType

class UploadProductImageUseCase(
    private val imageStorageService: ImageStorageService,
) {
    companion object {
        private const val MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024
    }

    suspend operator fun invoke(
        productId: ProductId,
        fileBytes: ByteArray,
    ): DomainResult<String> {
        if (fileBytes.size > MAX_FILE_SIZE_BYTES) {
            return DomainResult.Error(
                "Image size exceeds maximum file size limit",
                ErrorType.BAD_REQUEST
            )
        }

        val jpgBytes = convertToJpgBytes(fileBytes)
        val filename = "${productId.value}.jpg"

        return imageStorageService.saveImage(jpgBytes, filename)
    }
}