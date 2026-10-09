package api.route

import api.dto.request.CategoryRequest
import api.mapper.toDomain
import util.toHttpStatusCode
import api.mapper.toResponse
import domain.model.CategoryId
import result.DomainResult
import domain.usecase.category.CategoryUseCases
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.resources.post
import kotlinx.serialization.Serializable
import model.AppRole
import model.GroupId
import org.koin.ktor.ext.inject
import resource.GroupResource
import usecase.VerifyUserGroupRoleUseCase
import util.verifyGroupAccess

@Serializable
@Resource("category")
class CategoryResource (val parent: GroupResource.Id) {
    @Serializable
    @Resource("{categoryId}")
    class Id(val parent: CategoryResource, val categoryId: String)
}

fun Route.categoryRoutes() {
    val useCases: CategoryUseCases by inject()
    val verifyUserGroupRoleUseCase: VerifyUserGroupRoleUseCase by inject()

    authenticate("auth-jwt") {
        // Get all category for current group
        get <CategoryResource> { request ->
            val groupId = GroupId(request.parent.groupId)

            call.verifyGroupAccess(
                groupId = groupId,
                verifyUserGroupRole = verifyUserGroupRoleUseCase,
            ) ?: return@get

            when(
                val result = useCases.getAllCategory(
                    groupId = groupId
                )
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.map { it.toResponse() })
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }

        // Get category
        get <CategoryResource.Id> { request ->
            val groupId = GroupId(request.parent.parent.groupId)
            val categoryId = CategoryId(request.categoryId)

            call.verifyGroupAccess(
                groupId = groupId,
                verifyUserGroupRole = verifyUserGroupRoleUseCase,
            ) ?: return@get

            when (
                val result = useCases.getCategoryById(
                    groupId = groupId,
                    categoryId = categoryId
                )
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.toResponse())
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }

        // Insert category
        rateLimit (RateLimitName("upload_limit")) {
            post <CategoryResource> { request ->
                val groupId = GroupId(request.parent.groupId)

                call.verifyGroupAccess(
                    groupId = groupId,
                    verifyUserGroupRole = verifyUserGroupRoleUseCase,
                    allowedRoles = listOf(AppRole.OWNER, AppRole.ADMIN)
                ) ?: return@post

                val categoryRequest = call.receive<CategoryRequest>()
                val category = categoryRequest.toDomain(groupId)

                when(
                    val result = useCases.insertCategory(
                        category = category,
                        groupId = groupId
                    )
                ) {
                    is DomainResult.Success -> call.respond(HttpStatusCode.Created, category.toResponse())
                    is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
                }
            }
        }

        // Update Category
        rateLimit (RateLimitName("upload_limit")) {
            put <CategoryResource.Id> { request ->
                val groupId = GroupId(request.parent.parent.groupId)

                call.verifyGroupAccess(
                    groupId = groupId,
                    verifyUserGroupRole = verifyUserGroupRoleUseCase,
                    allowedRoles = listOf(AppRole.OWNER, AppRole.ADMIN)
                ) ?: return@put

                val categoryId = CategoryId(request.categoryId)
                val categoryRequest = call.receive<CategoryRequest>()
                val updatedCategory = categoryRequest.toDomain(groupId)

                when(
                    val result = useCases.updateCategory(
                        categoryId = categoryId,
                        groupId = groupId,
                        category = updatedCategory
                    )
                ) {
                    is DomainResult.Success -> call.respond(HttpStatusCode.OK, updatedCategory.toResponse())
                    is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
                }
            }
        }

        // Delete category
        delete <CategoryResource.Id> { request ->
            val groupId = GroupId(request.parent.parent.groupId)
            val categoryId = CategoryId(request.categoryId)

            call.verifyGroupAccess(
                groupId = groupId,
                verifyUserGroupRole = verifyUserGroupRoleUseCase,
                allowedRoles = listOf(AppRole.OWNER, AppRole.ADMIN)
            ) ?: return@delete

            when(
                val result = useCases.deleteCategory(
                    categoryId = categoryId,
                    groupId = groupId
                )
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.NoContent)
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }
    }
}