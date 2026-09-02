package api

import api.mapper.toHttpStatusCode
import domain.model.BatchId
import domain.model.StockBatch
import domain.result.DomainResult
import domain.usecase.stockbatch.StockBatchUseCases
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.put
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
@Resource("stockBatch")
class StockBatchResource{
    @Serializable
    @Resource("{id}")
    class Id(val parent: StockBatchResource = StockBatchResource(), val id: String)
}

fun Route.stockBatchRoutes() {
    val useCases: StockBatchUseCases by inject()

    get <StockBatchResource.Id> { request ->
        val batchId = BatchId(request.id)

        when(
            val result = useCases.getBatchById(batchId)
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data)
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }
    }

    post < StockBatchResource> {
        val batch = call.receive<StockBatch>()

        when(
            val result = useCases.insertBatch(batch)
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.Created, batch)
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }
    }

    put<StockBatchResource.Id> { request ->
        val batchId = BatchId(request.id)
        val batch = call.receive<StockBatch>()

        when(
            val result = useCases.updateBatch(batchId, batch)
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.OK, batch)
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }
    }

    delete<StockBatchResource.Id> { request ->
        val batchId = BatchId(request.id)

        when(
            val result = useCases.deleteBatch(batchId)
        ) {
            is DomainResult.Success -> call.respond(HttpStatusCode.NoContent)
            is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
        }
    }
}