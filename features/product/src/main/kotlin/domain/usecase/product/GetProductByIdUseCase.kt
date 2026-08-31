package domain.usecase.product

import domain.model.Product
import domain.model.ProductId
import domain.repository.ProductRepository
import domain.result.DomainResult
import domain.result.ErrorType

class GetProductByIdUseCase (
    private val repository: ProductRepository
) {
    suspend operator fun invoke(productId: ProductId): DomainResult<Product> {
        return when (
            val result = repository.getProductById(productId)
        ) {
            is DomainResult.Success -> {
                if (result.data == null) {
                    DomainResult.Error("Product Not Found", ErrorType.NOT_FOUND)
                } else {
                    DomainResult.Success(result.data)
                }
            }
            is DomainResult.Error -> result
        }
    }
}