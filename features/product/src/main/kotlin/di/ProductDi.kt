package di

import data.repository.CategoryRepositoryImpl
import data.repository.LocationRepositoryImpl
import data.repository.ProductRepositoryImpl
import data.repository.StockBatchRepositoryImpl
import domain.repository.CategoryRepository
import domain.repository.LocationRepository
import domain.repository.ProductRepository
import domain.repository.StockBatchRepository
import domain.usecase.category.CategoryUseCases
import domain.usecase.category.DeleteCategoryUseCase
import domain.usecase.category.GetAllCategoryUseCase
import domain.usecase.category.GetCategoryByIdUseCase
import domain.usecase.category.InsertCategoryUseCase
import domain.usecase.category.UpdateCategoryUseCase
import domain.usecase.location.DeleteLocationUseCase
import domain.usecase.location.GetAllLocationUseCase
import domain.usecase.location.GetLocationByIdUseCase
import domain.usecase.location.InsertLocationUseCase
import domain.usecase.location.LocationUseCases
import domain.usecase.location.UpdateLocationUseCase
import domain.usecase.product.DeleteProductUseCase
import domain.usecase.product.GetAllProductUseCase
import domain.usecase.product.GetProductByIdUseCase
import domain.usecase.product.InsertProductUseCase
import domain.usecase.product.ProductUseCases
import domain.usecase.product.UpdateProductUseCase
import domain.usecase.stockbatch.DeleteStockBatchUseCase
import domain.usecase.stockbatch.GetStockBatchByIdUseCase
import domain.usecase.stockbatch.InsertStockBatchUseCase
import domain.usecase.stockbatch.StockBatchUseCases
import domain.usecase.stockbatch.UpdateStockBatchUseCase
import org.koin.core.scope.get
import org.koin.dsl.module

val productModule = module {
    //// Data Layer (Repositories)
    single<ProductRepository> { ProductRepositoryImpl() }
    single<CategoryRepository> { CategoryRepositoryImpl() }
    single<LocationRepository> { LocationRepositoryImpl() }
    single<StockBatchRepository> { StockBatchRepositoryImpl() }

    //// Domain Layer (UseCases)
    // Product Use Cases
    factory { GetAllProductUseCase(get()) }
    factory { GetProductByIdUseCase(get()) }
    factory { InsertProductUseCase(get()) }
    factory { UpdateProductUseCase(get()) }
    factory { DeleteProductUseCase(get()) }
    factory {
        ProductUseCases(
            getAllProduct = get(),
            getProductById = get(),
            insertProduct = get(),
            updateProduct = get(),
            deleteProduct = get()
        )
    }

    // Category Use Cases
    factory { GetAllCategoryUseCase(get()) }
    factory { GetCategoryByIdUseCase(get()) }
    factory { InsertCategoryUseCase(get()) }
    factory { UpdateCategoryUseCase(get()) }
    factory { DeleteCategoryUseCase(get()) }
    factory {
        CategoryUseCases(
            getAllCategory = get(),
            getCategoryById = get(),
            insertCategory = get(),
            updateCategory = get(),
            deleteCategory = get(),
        )
    }

    // Location Use Cases
    factory { GetAllLocationUseCase(get()) }
    factory { GetLocationByIdUseCase(get()) }
    factory { InsertLocationUseCase(get()) }
    factory { UpdateLocationUseCase(get()) }
    factory { DeleteLocationUseCase(get()) }
    factory {
        LocationUseCases(
            getAllLocation = get(),
            getLocationById = get(),
            insertLocation = get(),
            updateLocation = get(),
            deleteLocation = get(),
        )
    }

    // Stock Batch Use Cases
    factory { GetStockBatchByIdUseCase(get()) }
    factory { InsertStockBatchUseCase(get()) }
    factory { UpdateStockBatchUseCase(get()) }
    factory { DeleteStockBatchUseCase(get()) }
    factory {
        StockBatchUseCases(
            getBatchById = get(),
            insertBatch = get(),
            updateBatch = get(),
            deleteBatch = get(),
        )
    }

}