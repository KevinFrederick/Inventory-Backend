package domain.usecase.product

import domain.model.Product
import domain.model.ProductId
import domain.repository.ProductRepository
import result.DomainResult
import result.ErrorType

class GetProductByIdUseCase (
    private val repository: ProductRepository
) {
    suspend operator fun invoke(productId: ProductId): DomainResult<Product> {
        return when (
            val result = repository.getProductById(productId)
        ) {
            is DomainResult.Success -> {
                val product = result.data

                if (product == null) {
                    DomainResult.Error("Product Not Found", ErrorType.NOT_FOUND)
                } else {
                    DomainResult.Success(product)
                }
            }
            is DomainResult.Error -> result
        }
    }
}