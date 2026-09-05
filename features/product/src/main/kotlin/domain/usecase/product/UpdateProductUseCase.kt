package domain.usecase.product

import domain.model.Product
import domain.model.ProductId
import domain.model.ProductParams
import domain.repository.CategoryRepository
import domain.repository.ProductRepository
import domain.usecase.util.StockBatchAssembler
import result.DomainResult
import result.ErrorType

class UpdateProductUseCase (
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val stockBatchAssembler: StockBatchAssembler,
) {
    suspend operator fun invoke(
        productId: ProductId,
        productParams: ProductParams
    ): DomainResult<Product> {
        return if (productId != productParams.productId) {
            DomainResult.Error("Product Id doesn't match", ErrorType.BAD_REQUEST)
        } else {
            val categoryResult = categoryRepository.getCategoryById(productParams.categoryId)
            val category = (categoryResult as? DomainResult.Success)?.data
                ?: return categoryResult as DomainResult.Error

            val batchesResult = stockBatchAssembler.assembleBatches(productParams.batches)
            val batches = (batchesResult as? DomainResult.Success)?.data
                ?: return batchesResult as DomainResult.Error

            val product = Product(
                productId = productParams.productId,
                category = category,
                name = productParams.name,
                description = productParams.description,
                barcode = productParams.barcode,
                sku = productParams.sku,
                imageUri = productParams.imageUri,
                minimumQuantity = productParams.minimumQuantity,
                batches = batches,
                createdAt = productParams.createdAt,
                lastUpdated = productParams.lastUpdated
            )

            productRepository.updateProduct(product)
        }

    }
}