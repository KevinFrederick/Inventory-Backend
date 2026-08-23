package domain.repository

import domain.model.Product
import domain.model.ProductId

interface ProductRepository {
    suspend fun getProducts(): List<Product>
    suspend fun getProductById(productId: ProductId): Product?
    suspend fun insertProduct(product: Product): Boolean
    suspend fun updateProduct(product: Product): Boolean
    suspend fun deleteProduct(productId: ProductId): Boolean
}