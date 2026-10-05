package api.route

import util.toHttpStatusCode
import domain.model.BatchId
import api.dto.request.StockBatchRequest
import api.mapper.toDomainParams
import api.mapper.toResponse
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
import model.AppRole
import model.GroupId
import org.koin.ktor.ext.inject
import resource.GroupResource
import usecase.VerifyUserGroupRoleUseCase
import util.verifyGroupAccess

@Serializable
@Resource("stockBatch")
class StockBatchResource (val parent: GroupResource.Id){
    @Serializable
    @Resource("{batchId}")
    class Id(val parent: StockBatchResource, val batchId: String)
}

fun Route.stockBatchRoutes() {
    val useCases: StockBatchUseCases by inject()
    val verifyUserGroupRoleUseCase: VerifyUserGroupRoleUseCase by inject()

    authenticate("auth-jwt") {
        // Get stock batch
        get <StockBatchResource.Id> { request ->
            val groupId = GroupId(request.parent.parent.groupId)
            val batchId = BatchId(request.batchId)

            call.verifyGroupAccess(
                groupId = groupId,
                verifyUserGroupRole = verifyUserGroupRoleUseCase,
            ) ?: return@get

            when(
                val result = useCases.getBatchById(
                    batchId = batchId,
                    groupId = groupId
                )
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.toResponse())
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }

        // Insert batch
        rateLimit (RateLimitName("upload_limit")) {
            post < StockBatchResource> {request ->
                val groupId = GroupId(request.parent.groupId)
                val batchRequest = call.receive<StockBatchRequest>()
                val batchParams = batchRequest.toDomainParams()

                call.verifyGroupAccess(
                    groupId = groupId,
                    verifyUserGroupRole = verifyUserGroupRoleUseCase,
                ) ?: return@post

                when(
                    val result = useCases.insertBatch(
                        batchParams = batchParams,
                        groupId = groupId
                    )
                ) {
                    is DomainResult.Success -> call.respond(HttpStatusCode.Created, result.data.toResponse())
                    is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
                }
            }
        }

        // Update stock batch
        rateLimit (RateLimitName("upload_limit")) {
            put<StockBatchResource.Id> { request ->
                val groupId = GroupId(request.parent.parent.groupId)
                val batchId = BatchId(request.batchId)
                val batchRequest = call.receive<StockBatchRequest>()
                val batchParams = batchRequest.toDomainParams()

                call.verifyGroupAccess(
                    groupId = groupId,
                    verifyUserGroupRole = verifyUserGroupRoleUseCase,
                ) ?: return@put

                when(
                    val result = useCases.updateBatch(
                        batchId = batchId,
                        batchParams = batchParams,
                        groupId = groupId
                    )
                ) {
                    is DomainResult.Success -> call.respond(HttpStatusCode.OK, result.data.toResponse())
                    is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
                }
            }
        }

        // Delete stock batch
        delete<StockBatchResource.Id> { request ->
            val groupId = GroupId(request.parent.parent.groupId)
            val batchId = BatchId(request.batchId)

            call.verifyGroupAccess(
                groupId = groupId,
                verifyUserGroupRole = verifyUserGroupRoleUseCase,
                allowedRoles = listOf(AppRole.OWNER, AppRole.ADMIN)
            ) ?: return@delete

            when(
                val result = useCases.deleteBatch(
                    batchId = batchId,
                    groupId = groupId
                )
            ) {
                is DomainResult.Success -> call.respond(HttpStatusCode.NoContent)
                is DomainResult.Error -> call.respond(result.errorType.toHttpStatusCode(), result.message)
            }
        }
    }
}