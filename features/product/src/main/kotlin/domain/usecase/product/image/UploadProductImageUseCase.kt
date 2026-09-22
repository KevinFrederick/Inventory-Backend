package domain.usecase.product.image

import domain.ImageStorageService
import domain.model.ProductId
import domain.repository.ProductRepository
import domain.usecase.util.convertToJpgBytes
import result.DomainResult
import result.ErrorType

class UploadProductImageUseCase(
    private val imageStorageService: ImageStorageService,
    private val repository: ProductRepository
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

        val productResult = repository.getProductById(productId)
        if (productResult is DomainResult.Error) {
            return productResult
        }

        val jpgBytes = convertToJpgBytes(fileBytes)
        val filename = "${productId.value}.jpg"

        when(
            val storageResult = imageStorageService.saveImage(jpgBytes, filename)
        ) {
            is DomainResult.Error -> return storageResult

            is DomainResult.Success -> {
                val updateResult = repository.updateProductImage(
                    productId = productId,
                    imageUriPath = storageResult.data,
                    updatedAt = System.currentTimeMillis()
                )

                return when (updateResult) {
                    is DomainResult.Success -> storageResult
                    is DomainResult.Error -> updateResult
                }
            }
        }
    }
}