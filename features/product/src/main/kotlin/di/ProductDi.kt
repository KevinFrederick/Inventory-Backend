package di

import data.repository.CategoryRepositoryImpl
import data.repository.LocationRepositoryImpl
import data.repository.ProductRepositoryImpl
import data.repository.StockBatchRepositoryImpl
import data.repository.SyncRepositoryImpl
import domain.repository.CategoryRepository
import domain.repository.LocationRepository
import domain.repository.ProductRepository
import domain.repository.StockBatchRepository
import domain.repository.SyncRepository
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
import domain.usecase.product.image.DeleteProductImageUseCase
import domain.usecase.product.image.UploadProductImageUseCase
import domain.usecase.stockbatch.DeleteStockBatchUseCase
import domain.usecase.stockbatch.GetStockBatchByIdUseCase
import domain.usecase.stockbatch.InsertStockBatchUseCase
import domain.usecase.util.StockBatchAssembler
import domain.usecase.stockbatch.StockBatchUseCases
import domain.usecase.stockbatch.UpdateStockBatchUseCase
import domain.usecase.sync.SyncPullUseCase
import domain.usecase.sync.SyncPushUseCase
import domain.usecase.sync.SyncUseCase
import domain.validation.CategoryValidator
import domain.validation.LocationValidator
import domain.validation.ProductValidator
import domain.validation.StockBatchValidator
import org.koin.core.qualifier.named
import org.koin.dsl.module

val productModule = module {
    //// Data Layer (Repositories)
    single<ProductRepository> { ProductRepositoryImpl() }
    single<CategoryRepository> { CategoryRepositoryImpl() }
    single<LocationRepository> { LocationRepositoryImpl() }
    single<StockBatchRepository> { StockBatchRepositoryImpl() }
    single<SyncRepository> { SyncRepositoryImpl() }

    //// Domain Layer (UseCases)
    // Assembler
    single<StockBatchAssembler> { StockBatchAssembler(get()) }

    // Product Use Cases
    factory { GetAllProductUseCase(get()) }
    factory { GetProductByIdUseCase(get()) }
    factory { InsertProductUseCase(get(), get(), get(), get()) }
    factory { UpdateProductUseCase(get(), get(), get(), get()) }
    factory { UploadProductImageUseCase(get(named("ProductStorage")), get())}
    factory { DeleteProductUseCase(get()) }
    factory { DeleteProductImageUseCase(get(named("ProductStorage")), get()) }
    factory {
        ProductUseCases(
            getAllProduct = get(),
            getProductById = get(),
            insertProduct = get(),
            updateProduct = get(),
            uploadProductImage = get(),
            deleteProduct = get(),
            deleteProductImage = get()
        )
    }

    // Category Use Cases
    factory { GetAllCategoryUseCase(get()) }
    factory { GetCategoryByIdUseCase(get()) }
    factory { InsertCategoryUseCase(get(), get()) }
    factory { UpdateCategoryUseCase(get(), get()) }
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
    factory { InsertLocationUseCase(get(), get()) }
    factory { UpdateLocationUseCase(get(), get()) }
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
    factory { InsertStockBatchUseCase(get(), get(), get(), get()) }
    factory { UpdateStockBatchUseCase(get(), get(), get(), get()) }
    factory { DeleteStockBatchUseCase(get()) }
    factory {
        StockBatchUseCases(
            getBatchById = get(),
            insertBatch = get(),
            updateBatch = get(),
            deleteBatch = get(),
        )
    }

    factory { SyncPushUseCase(get(), get(), get(), get(), get()) }
    factory { SyncPullUseCase(get()) }
    factory {
        SyncUseCase(
            syncPull = get(),
            syncPush = get()
        )
    }

    // Validator
    factory { LocationValidator() }
    factory { CategoryValidator() }
    factory { StockBatchValidator(get()) }
    factory { ProductValidator(get(), get()) }
}