package domain.repository

import domain.model.sync.SyncPayload
import domain.model.sync.SyncPullResponse
import domain.model.sync.SyncPushResponse
import model.GroupId
import result.DomainResult

interface SyncRepository {
    suspend fun pushSync(syncPayload: SyncPayload, groupId: GroupId): DomainResult<SyncPushResponse>
    suspend fun pullSync(updatedAfter: Long, groupId: GroupId): DomainResult<SyncPullResponse>
}