package domain.usecase.product

import domain.model.Product
import domain.repository.ProductRepository
import domain.result.DomainResult

class InsertProductUseCase (
    private val repository: ProductRepository
) {
    suspend operator fun invoke(product: Product): DomainResult<Unit> =
        repository.insertProduct(product)
}