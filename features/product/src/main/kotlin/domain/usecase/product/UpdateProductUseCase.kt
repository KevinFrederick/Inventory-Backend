package domain.usecase.product

import domain.model.Product
import domain.model.ProductId
import domain.model.ProductParams
import domain.repository.CategoryRepository
import domain.repository.ProductRepository
import domain.usecase.util.StockBatchAssembler
import domain.validation.ProductValidator
import result.DomainResult
import result.ErrorType

class UpdateProductUseCase (
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val stockBatchAssembler: StockBatchAssembler,
    private val productValidator: ProductValidator,
) {
    suspend operator fun invoke(
        productId: ProductId,
        productParams: ProductParams
    ): DomainResult<Product> {
        return if (productId != productParams.productId) {
            DomainResult.Error("Product Id doesn't match", ErrorType.BAD_REQUEST)
        } else {
            val category = when (
                val categoryResult = categoryRepository.getCategoryById(productParams.categoryId)
            ) {
                is DomainResult.Success -> {
                    categoryResult.data ?: return DomainResult.Error("Category not found", ErrorType.NOT_FOUND)
                }
                is DomainResult.Error -> return categoryResult
            }

            val batches = when (
                val batchesResult = stockBatchAssembler.assembleBatches(productParams.batches)
            ) {
                is DomainResult.Success -> batchesResult.data
                is DomainResult.Error -> return batchesResult
            }

            val product = Product(
                productId = productParams.productId,
                category = category,
                name = productParams.name,
                description = productParams.description,
                barcode = productParams.barcode,
                barcodeFormat = productParams.barcodeFormat,
                sku = productParams.sku,
                imageUri = productParams.imageUri,
                minimumQuantity = productParams.minimumQuantity,
                batches = batches,
                createdAt = productParams.createdAt,
                lastUpdated = productParams.lastUpdated
            )

            when(
                val validationResult = productValidator.validateProduct(product)
            ) {
                is DomainResult.Error -> return validationResult
                is DomainResult.Success -> {}
            }

            productRepository.updateProduct(product)
        }

    }
}