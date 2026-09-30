package api.route

import util.toHttpStatusCode
import domain.model.BatchId
import api.dto.request.StockBatchRequest
import api.mapper.toDomainParams
import api.mapper.toResponse
import api.util.userId
import result.DomainResult
import domain.usecase.stockbatch.StockBatchUseCases
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.resources.put
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

    authenticate("auth-jwt") {
        get <StockBatchResource.Id> { request ->
            val batchId = BatchId(request.id)

            when(
                val result = useCases.getBatchById(batchId)
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.toResponse())
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }

        rateLimit (RateLimitName("upload_limit")) {
            post < StockBatchResource> {
                val batchRequest = call.receive<StockBatchRequest>()
                val batchParams = batchRequest.toDomainParams()

                when(
                    val result = useCases.insertBatch(batchParams)
                ) {
                    is DomainResult.Success -> call.respond(HttpStatusCode.Created, result.data.toResponse())
                    is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
                }
            }
        }

        rateLimit (RateLimitName("upload_limit")) {
            put<StockBatchResource.Id> { request ->
                val batchId = BatchId(request.id)
                val batchRequest = call.receive<StockBatchRequest>()
                val batchParams = batchRequest.toDomainParams()

                when(
                    val result = useCases.updateBatch(batchId, batchParams)
                ) {
                    is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.toResponse())
                    is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
                }
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
}