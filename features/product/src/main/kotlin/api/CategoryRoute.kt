package api

import api.mapper.toHttpStatusCode
import domain.model.Category
import domain.model.CategoryId
import domain.result.DomainResult
import domain.usecase.category.CategoryUseCases
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
@Resource("/category")
class CategoryResource {
    @Serializable
    @Resource("{id}")
    class Id(val parent: CategoryResource = CategoryResource(), val id: String)
}

fun Route.categoryRoute() {
    val useCases: CategoryUseCases by inject()

    get <CategoryResource> {
        when(
            val result = useCases.getAllCategory()
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }
    }

    get <CategoryResource.Id> { request ->
        val categoryId = CategoryId(request.id)

        when (
            val result = useCases.getCategoryById(categoryId)
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }
    }

    post <CategoryResource> {
        val category = call.receive<Category>()

        when(
            val result = useCases.insertCategory(category)
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.Created, category)
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }
    }

    put <CategoryResource.Id> { request ->
        val categoryId = CategoryId(request.id)
        val updatedCategory = call.receive<Category>()

        when(
            val result = useCases.updateCategory(categoryId, updatedCategory)
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.OK, updatedCategory)
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