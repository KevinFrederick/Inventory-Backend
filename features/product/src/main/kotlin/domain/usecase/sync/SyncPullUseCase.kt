package domain.usecase.sync

import domain.model.sync.SyncPullResponse
import domain.repository.SyncRepository
import result.DomainResult

class SyncPullUseCase (
    private val repository: SyncRepository
) {
    suspend operator fun invoke(updatedAfter: Long): DomainResult<SyncPullResponse> =
        repository.pullSync(updatedAfter)
}