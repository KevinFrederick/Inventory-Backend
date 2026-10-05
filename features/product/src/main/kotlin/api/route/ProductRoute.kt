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
import model.AppRole
import model.GroupId
import org.koin.ktor.ext.inject
import resource.GroupResource
import result.DomainResult
import usecase.VerifyUserGroupRoleUseCase
import util.verifyGroupAccess

@Serializable
@Resource("product")
class ProductResource (val parent: GroupResource.Id) {

    @Serializable
    @Resource("{productId}")
    class Id(val parent: ProductResource, val productId: String) {

        @Serializable
        @Resource("image")
        class ProductImage(val parent: Id)
    }
}

fun Route.productRoutes() {
    val useCases: ProductUseCases by inject()
    val verifyUserGroupRoleUseCase: VerifyUserGroupRoleUseCase by inject()

    authenticate("auth-jwt") {
        // Get all products for current group
        get <ProductResource> { request ->
            val groupId = GroupId(request.parent.groupId)

            call.verifyGroupAccess(
                groupId = groupId,
                verifyUserGroupRole = verifyUserGroupRoleUseCase,
            ) ?: return@get

            when (
                val result = useCases.getAllProduct(
                    groupId = groupId,
                )
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.map { it.toResponse() })
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }

        // Get product
        get<ProductResource.Id> { request ->
            val groupId = GroupId(request.parent.parent.groupId)
            val productId = ProductId(request.productId)

            call.verifyGroupAccess(
                groupId = groupId,
                verifyUserGroupRole = verifyUserGroupRoleUseCase,
            ) ?: return@get

            when(
                val result = useCases.getProductById(
                    productId = productId,
                    groupId = groupId,
                )
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.toResponse())
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }

        // Insert Product
        rateLimit (RateLimitName("upload_limit")) {
            post<ProductResource> { request ->
                val groupId = GroupId(request.parent.groupId)
                val productRequest = call.receive<ProductRequest>()
                val productParams = productRequest.toDomainParams()

                call.verifyGroupAccess(
                    groupId = groupId,
                    verifyUserGroupRole = verifyUserGroupRoleUseCase,
                    allowedRoles = listOf(AppRole.OWNER, AppRole.ADMIN)
                ) ?: return@post

                when (
                    val result = useCases.insertProduct(
                        productParams = productParams,
                        groupId = groupId
                    )
                ) {
                    is DomainResult.Success -> call.respond(HttpStatusCode.Created, result.data.toResponse())
                    is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
                }
            }
        }

        // Update Product
        rateLimit (RateLimitName("upload_limit")) {
            put<ProductResource.Id> { request ->
                val groupId = GroupId(request.parent.parent.groupId)
                val productId = ProductId(request.productId)
                val productRequest = call.receive<ProductRequest>()
                val productParams = productRequest.toDomainParams()

                call.verifyGroupAccess(
                    groupId = groupId,
                    verifyUserGroupRole = verifyUserGroupRoleUseCase,
                    allowedRoles = listOf(AppRole.OWNER, AppRole.ADMIN)
                ) ?: return@put

                when (
                    val result = useCases.updateProduct(
                        productId = productId,
                        productParams = productParams,
                        groupId = groupId
                    )
                ) {
                    is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.toResponse())
                    is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
                }
            }
        }

        // Upload product image
        rateLimit (RateLimitName("upload_limit")) {
            put<ProductResource.Id.ProductImage> { request ->
                val groupId = GroupId(request.parent.parent.parent.groupId)
                val productId = ProductId(request.parent.productId)

                call.verifyGroupAccess(
                    groupId = groupId,
                    verifyUserGroupRole = verifyUserGroupRoleUseCase,
                    allowedRoles = listOf(AppRole.OWNER, AppRole.ADMIN)
                ) ?: return@put

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
                    val result = useCases.uploadProductImage(
                        productId = productId,
                        fileBytes = finalBytes,
                        groupId = groupId
                    )
                ) {
                    is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                    is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
                }
            }
        }

        // Delete Product
        delete<ProductResource.Id>{ request ->
            val groupId = GroupId(request.parent.parent.groupId)
            val productId = ProductId(request.productId)

            call.verifyGroupAccess(
                groupId = groupId,
                verifyUserGroupRole = verifyUserGroupRoleUseCase,
                allowedRoles = listOf(AppRole.OWNER, AppRole.ADMIN)
            ) ?: return@delete

            when(
                val result = useCases.deleteProduct(
                    productId = productId,
                    groupId = groupId
                )
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.NoContent)
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }

        // Delete product image
        delete<ProductResource.Id.ProductImage> { request ->
            val groupId = GroupId(request.parent.parent.parent.groupId)
            val productId = ProductId(request.parent.productId)

            call.verifyGroupAccess(
                groupId = groupId,
                verifyUserGroupRole = verifyUserGroupRoleUseCase,
                allowedRoles = listOf(AppRole.OWNER, AppRole.ADMIN)
            ) ?: return@delete

            when(
                val result = useCases.deleteProductImage(
                    productId = productId,
                    groupId = groupId
                )
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.NoContent)
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }
    }
}