package domain.usecase.product

data class ProductUseCases(
    val getAllProduct: GetAllProductUseCase,
    val getProductById: GetProductByIdUseCase,
    val insertProduct: InsertProductUseCase,
    val updateProduct: UpdateProductUseCase,
    val deleteProduct: DeleteProductUseCase,
)
