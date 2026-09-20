package domain.repository

import domain.model.sync.SyncPayload
import domain.model.sync.SyncPullResponse
import domain.model.sync.SyncPushResponse
import result.DomainResult

interface SyncRepository {
    suspend fun pushSync(syncPayload: SyncPayload): DomainResult<SyncPushResponse>
    suspend fun pullSync(updatedAfter: Long): DomainResult<SyncPullResponse>
}