package domain.usecase.product.image

import domain.ImageStorageService
import domain.model.ProductId
import domain.repository.ProductRepository
import domain.usecase.util.convertToJpgBytes
import result.DomainResult

class UploadProductImageUseCase(
    private val imageStorageService: ImageStorageService,
    private val repository: ProductRepository
) {
    suspend operator fun invoke(
        productId: ProductId,
        fileBytes: ByteArray,
    ): DomainResult<String> {
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