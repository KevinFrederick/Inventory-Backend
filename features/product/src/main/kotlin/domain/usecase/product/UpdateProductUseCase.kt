package domain.usecase.product

import domain.model.Product
import domain.model.ProductId
import domain.repository.ProductRepository
import domain.result.DomainResult
import domain.result.ErrorType

class UpdateProductUseCase (
    private val repository: ProductRepository,
) {
    suspend operator fun invoke(
        productId: ProductId,
        product: Product
    ): DomainResult<Unit> {
        return if (productId != product.productId) {
            DomainResult.Error("Product Id doesn't match", ErrorType.BAD_REQUEST)
        } else {
            repository.updateProduct(product)
        }

    }
}