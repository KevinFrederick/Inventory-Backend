package api

import api.mapper.toHttpStatusCode
import domain.model.Product
import domain.model.ProductId
import domain.result.DomainResult
import domain.usecase.product.ProductUseCases
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
@Resource("/products")
class ProductRoute {

    @Serializable
    @Resource("{id}")
    class Id(val parent: ProductRoute = ProductRoute(), val id: String)
}

fun Route.productRoutes() {
    val useCases: ProductUseCases by inject()

    get <ProductRoute> {
        when (
            val result = useCases.getAllProduct()
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }
    }

    post<ProductRoute> {
        val product = call.receive<Product>()
        when (
            val result = useCases.insertProduct(product)
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.Created, product)
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }
    }

    get<ProductRoute.Id> { request ->
        val productId = ProductId(request.id)
        when(
            val result = useCases.getProductById(productId)
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }
    }

    put<ProductRoute.Id> { request ->
        val productId = ProductId(request.id)
        val updatedProduct = call.receive<Product>()

        when (
            val result = useCases.updateProduct(productId, updatedProduct)
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }
    }

    delete<ProductRoute.Id>{ request ->
        val productId = ProductId(request.id)
        when(
            val result = useCases.deleteProduct(productId)
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.NoContent)
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }
    }
}