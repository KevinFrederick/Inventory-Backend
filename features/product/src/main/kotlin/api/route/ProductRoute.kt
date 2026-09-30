package api.route

import util.toHttpStatusCode
import domain.model.ProductId
import api.dto.request.ProductRequest
import api.mapper.toDomainParams
import api.mapper.toResponse
import domain.usecase.product.ProductUseCases
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.resources.Resource
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.utils.io.toByteArray
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import result.DomainResult

@Serializable
@Resource("/product")
class ProductResource {

    @Serializable
    @Resource("{id}")
    class Id(val parent: ProductResource = ProductResource(), val id: String) {

        @Serializable
        @Resource("image")
        class ProductImage(val parent: Id)
    }
}

fun Route.productRoutes() {
    val useCases: ProductUseCases by inject()
    authenticate("auth-jwt") {
        get <ProductResource> {
            when (
                val result = useCases.getAllProduct()
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.map { it.toResponse() })
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }

        get<ProductResource.Id> { request ->
            val productId = ProductId(request.id)
            when(
                val result = useCases.getProductById(productId)
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.toResponse())
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }

        rateLimit (RateLimitName("upload_limit")) {
            post<ProductResource> {
                val productRequest = call.receive<ProductRequest>()
                val productParams = productRequest.toDomainParams()

                when (
                    val result = useCases.insertProduct(productParams)
                ) {
                    is DomainResult.Success -> call.respond(HttpStatusCode.Created, result.data.toResponse())
                    is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
                }
            }
        }

        rateLimit (RateLimitName("upload_limit")) {
            put<ProductResource.Id> { request ->
                val productId = ProductId(request.id)
                val productRequest = call.receive<ProductRequest>()
                val productParams = productRequest.toDomainParams()

                when (
                    val result = useCases.updateProduct(productId, productParams)
                ) {
                    is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.toResponse())
                    is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
                }
            }
        }

        rateLimit (RateLimitName("upload_limit")) {
            put<ProductResource.Id.ProductImage> { request ->
                val productId = ProductId(request.parent.id)

                var fileBytes: ByteArray? = null

                val multipartData = call.receiveMultipart()
                multipartData.forEachPart { partData ->
                    when (partData) {
                        is PartData.FileItem -> {
                            fileBytes = partData.provider().toByteArray()
                        }
                        else -> {}
                    }

                    partData.release()
                }

                val finalBytes = fileBytes
                if (finalBytes == null) {
                    call.respond(HttpStatusCode.BadRequest, "No image file provided")
                    return@put
                }

                when(
                    val result = useCases.uploadProductImage(productId, finalBytes)
                ) {
                    is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                    is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
                }
            }
        }

        delete<ProductResource.Id>{ request ->
            val productId = ProductId(request.id)
            when(
                val result = useCases.deleteProduct(productId)
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.NoContent)
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }

        delete<ProductResource.Id.ProductImage> { request ->
            val productId = ProductId(request.parent.id)

            when(
                val result = useCases.deleteProductImage(productId)
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.NoContent)
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }
    }
}