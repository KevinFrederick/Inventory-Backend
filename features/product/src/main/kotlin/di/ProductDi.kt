package di

import data.repository.CategoryRepositoryImpl
import data.repository.LocationRepositoryImpl
import data.repository.ProductRepositoryImpl
import data.repository.StockBatchRepositoryImpl
import domain.repository.CategoryRepository
import domain.repository.LocationRepository
import domain.repository.ProductRepository
import domain.repository.StockBatchRepository
import org.koin.dsl.module

val productModule = module {
    single<ProductRepository> { ProductRepositoryImpl() }
    single<CategoryRepository> { CategoryRepositoryImpl() }
    single<LocationRepository> { LocationRepositoryImpl() }
    single<StockBatchRepository> { StockBatchRepositoryImpl() }
}