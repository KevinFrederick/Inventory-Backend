package domain.usecase.product

import model.GroupId
import domain.model.Product
import domain.model.ProductId
import domain.repository.ProductRepository
import result.DomainResult

class GetProductByIdUseCase (
    private val repository: ProductRepository
) {
    suspend operator fun invoke(
        productId: ProductId,
        groupId: GroupId
    ): DomainResult<Product> =
        repository.getProductById(productId, groupId)
}