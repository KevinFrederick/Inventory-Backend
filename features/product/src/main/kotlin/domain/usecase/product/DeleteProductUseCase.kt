package domain.usecase.product

import domain.model.ProductId
import domain.repository.ProductRepository
import domain.result.DomainResult

class DeleteProductUseCase (
    private val repository: ProductRepository,
) {
    suspend operator fun invoke(productId: ProductId): DomainResult<Unit> =
        repository.deleteProduct(productId)
}