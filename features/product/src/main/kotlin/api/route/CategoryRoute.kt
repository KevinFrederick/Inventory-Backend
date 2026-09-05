package api.route

import api.dto.request.CategoryRequest
import api.mapper.toDomain
import api.mapper.toHttpStatusCode
import api.mapper.toResponse
import domain.model.CategoryId
import result.DomainResult
import domain.usecase.category.CategoryUseCases
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.resources.post
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
@Resource("/category")
class CategoryResource {
    @Serializable
    @Resource("{id}")
    class Id(val parent: CategoryResource = CategoryResource(), val id: String)
}

fun Route.categoryRoutes() {
    val useCases: CategoryUseCases by inject()

    get <CategoryResource> {
        when(
            val result = useCases.getAllCategory()
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.map { it.toResponse() })
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }
    }

    get <CategoryResource.Id> { request ->
        val categoryId = CategoryId(request.id)

        when (
            val result = useCases.getCategoryById(categoryId)
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.toResponse())
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }
    }

    post <CategoryResource> {
        val categoryRequest = call.receive<CategoryRequest>()
        val category = categoryRequest.toDomain()

        when(
            val result = useCases.insertCategory(category)
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.Created, category.toResponse())
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }
    }

    put <CategoryResource.Id> { request ->
        val categoryId = CategoryId(request.id)
        val categoryRequest = call.receive<CategoryRequest>()
        val updatedCategory = categoryRequest.toDomain()

        when(
            val result = useCases.updateCategory(categoryId, updatedCategory)
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.OK, updatedCategory.toResponse())
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }
    }

    delete <CategoryResource.Id> { request ->
        val categoryId = CategoryId(request.id)

        when(
            val result = useCases.deleteCategory(categoryId)
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.NoContent)
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }
    }
}