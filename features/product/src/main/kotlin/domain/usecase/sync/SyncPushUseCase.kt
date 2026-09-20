package domain.usecase.sync

import domain.model.sync.SyncPayload
import domain.model.sync.SyncPushResponse
import domain.repository.SyncRepository
import result.DomainResult

class SyncPushUseCase (
    private val repository: SyncRepository
) {
    suspend operator fun invoke(syncPayload: SyncPayload): DomainResult<SyncPushResponse> =
        repository.pushSync(syncPayload)
}