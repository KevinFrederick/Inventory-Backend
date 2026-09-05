package domain.repository

import domain.model.Product
import domain.model.ProductId
import result.DomainResult

interface ProductRepository {
    suspend fun getProducts(): DomainResult<List<Product>>
    suspend fun getProductById(productId: ProductId): DomainResult<Product?>
    suspend fun insertProduct(product: Product): DomainResult<Product>
    suspend fun updateProduct(product: Product): DomainResult<Product>
    suspend fun updateProductImage(productId: ProductId, imageUriPath: String, updatedAt: Long): DomainResult<Unit>
    suspend fun deleteProduct(productId: ProductId): DomainResult<Unit>
    suspend fun deleteProductImage(productId: ProductId, updatedAt: Long): DomainResult<Unit>
}