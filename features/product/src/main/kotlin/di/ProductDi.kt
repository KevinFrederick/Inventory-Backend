package di

import data.repository.CategoryRepositoryImpl
import data.repository.LocationRepositoryImpl
import data.repository.ProductRepositoryImpl
import data.repository.StockBatchRepositoryImpl
import domain.repository.CategoryRepository
import domain.repository.LocationRepository
import domain.repository.ProductRepository
import domain.repository.StockBatchRepository
import domain.usecase.product.DeleteProductUseCase
import domain.usecase.product.GetAllProductUseCase
import domain.usecase.product.GetProductByIdUseCase
import domain.usecase.product.InsertProductUseCase
import domain.usecase.product.ProductUseCases
import domain.usecase.product.UpdateProductUseCase
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

    //
}