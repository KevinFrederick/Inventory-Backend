package domain.repository

import domain.model.Product
import domain.model.ProductId
import model.GroupId
import result.DomainResult

interface ProductRepository {
    suspend fun getProducts(groupId: GroupId): DomainResult<List<Product>>
    suspend fun getProductById(productId: ProductId, groupId: GroupId): DomainResult<Product>
    suspend fun insertProduct(product: Product, groupId: GroupId): DomainResult<Product>
    suspend fun updateProduct(product: Product, groupId: GroupId): DomainResult<Product>
    suspend fun updateProductImage(productId: ProductId, imageUriPath: String, updatedAt: Long, groupId: GroupId): DomainResult<Unit>
    suspend fun deleteProduct(productId: ProductId, groupId: GroupId): DomainResult<Unit>
    suspend fun deleteProductImage(productId: ProductId, updatedAt: Long, groupId: GroupId): DomainResult<Unit>
}