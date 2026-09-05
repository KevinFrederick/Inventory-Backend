package domain.usecase.product

import domain.model.Product
import domain.model.ProductParams
import domain.repository.CategoryRepository
import domain.repository.ProductRepository
import domain.usecase.util.StockBatchAssembler
import result.DomainResult
import result.ErrorType

class InsertProductUseCase (
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val stockBatchAssembler: StockBatchAssembler,
) {
    suspend operator fun invoke(productParams: ProductParams): DomainResult<Product> {
        val categoryResult = categoryRepository.getCategoryById(productParams.categoryId)
        val category = (categoryResult as? DomainResult.Success)?.data
            ?: return DomainResult.Error("Category not found", ErrorType.NOT_FOUND)

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

        return productRepository.insertProduct(product)
    }
}