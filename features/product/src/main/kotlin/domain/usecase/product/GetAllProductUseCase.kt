package domain.usecase.product

import model.GroupId
import domain.model.Product
import domain.repository.ProductRepository
import result.DomainResult

class GetAllProductUseCase (
    private val repository: ProductRepository
) {
    suspend operator fun invoke(
        groupId: GroupId
    ): DomainResult<List<Product>> =
        repository.getProducts(groupId)
}