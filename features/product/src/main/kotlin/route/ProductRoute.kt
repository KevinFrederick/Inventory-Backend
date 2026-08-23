package route

import io.ktor.resources.Resource
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import kotlinx.serialization.Serializable

@Serializable
@Resource("/products")
class ProductRoute {

    @Serializable
    @Resource("{id}")
    class Id(val parent: ProductRoute = ProductRoute(), val id: String)
}

fun Route.productRoutes() {
    get <ProductRoute> {
        call.respondText("Products")
    }

    post<ProductRoute> {
        call.respondText("Add Product")
    }

    get<ProductRoute.Id> { request ->
        call.respondText("Get ${request.id}")
    }

    put<ProductRoute.Id> { request ->
        call.respondText("Update ${request.id}")
    }

    delete<ProductRoute.Id>{ request ->
        call.respondText("Delete ${request.id}")
    }
}