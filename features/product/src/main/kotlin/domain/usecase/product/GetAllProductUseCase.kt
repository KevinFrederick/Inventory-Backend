package domain.usecase.product

import domain.model.Product
import domain.repository.ProductRepository
import result.DomainResult

class GetAllProductUseCase (
    private val repository: ProductRepository
) {
    suspend operator fun invoke(): DomainResult<List<Product>> =
        repository.getProducts()
}