package domain.repository

import domain.model.Product
import domain.model.ProductId
import domain.result.DomainResult

interface ProductRepository {
    suspend fun getProducts(): DomainResult<List<Product>>
    suspend fun getProductById(productId: ProductId): DomainResult<Product?>
    suspend fun insertProduct(product: Product): DomainResult<Unit>
    suspend fun updateProduct(product: Product): DomainResult<Unit>
    suspend fun deleteProduct(productId: ProductId): DomainResult<Unit>
}