package domain.usecase.product.image

import domain.model.ProductId
import domain.repository.ProductRepository
import result.DomainResult
import domain.ImageStorageService

class DeleteProductImageUseCase(
    private val imageStorageService: ImageStorageService,
    private val repository: ProductRepository
) {
    suspend operator fun invoke(productId: ProductId): DomainResult<Unit> {
        val imageResult = when (
            val productResult = repository.getProductById(productId)
        ) {
            is DomainResult.Success -> productResult.data?.imageUri
            is DomainResult.Error -> return productResult
        }

        if (imageResult == null) return DomainResult.Success(Unit)


        return when(
            val storageResult = imageStorageService.deleteImage(imageResult)
        ) {
            is DomainResult.Success -> {
                repository.deleteProductImage(
                    productId = productId,
                    updatedAt = System.currentTimeMillis(),
                )
            }
            is DomainResult.Error -> storageResult
        }
    }
}