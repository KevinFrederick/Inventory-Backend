package domain.usecase.product

import model.GroupId
import domain.model.ProductId
import domain.repository.ProductRepository
import result.DomainResult

class DeleteProductUseCase (
    private val repository: ProductRepository,
) {
    suspend operator fun invoke(
        productId: ProductId,
        groupId: GroupId,
    ): DomainResult<Unit> =
        repository.deleteProduct(productId, groupId)
}