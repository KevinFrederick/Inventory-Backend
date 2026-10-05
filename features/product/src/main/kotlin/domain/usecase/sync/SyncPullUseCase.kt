package domain.usecase.sync

import model.GroupId
import domain.model.sync.SyncPullResponse
import domain.repository.SyncRepository
import result.DomainResult

class SyncPullUseCase (
    private val repository: SyncRepository
) {
    suspend operator fun invoke(
        updatedAfter: Long,
        groupId: GroupId,
    ): DomainResult<SyncPullResponse> =
        repository.pullSync(updatedAfter, groupId)
}