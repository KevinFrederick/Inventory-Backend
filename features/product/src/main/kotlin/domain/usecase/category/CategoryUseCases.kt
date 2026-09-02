package domain.usecase.category

data class CategoryUseCases(
    val getAllCategory: GetAllCategoryUseCase,
    val getCategoryById: GetCategoryByIdUseCase,
    val insertCategory: InsertCategoryUseCase,
    val updateCategory: UpdateCategoryUseCase,
    val deleteCategory: DeleteCategoryUseCase,
)
