package domain.usecase.product

import domain.usecase.product.image.DeleteProductImageUseCase
import domain.usecase.product.image.UploadProductImageUseCase

data class ProductUseCases(
    val getAllProduct: GetAllProductUseCase,
    val getProductById: GetProductByIdUseCase,
    val insertProduct: InsertProductUseCase,
    val updateProduct: UpdateProductUseCase,
    val uploadProductImage: UploadProductImageUseCase,
    val deleteProduct: DeleteProductUseCase,
    val deleteProductImage: DeleteProductImageUseCase,
)
