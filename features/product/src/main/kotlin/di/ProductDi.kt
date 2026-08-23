import data.repository.ProductRepository
import domain.repository.IProductRepository
import org.koin.dsl.module

val productModule = module {
    single<IProductRepository> { ProductRepository() }
}