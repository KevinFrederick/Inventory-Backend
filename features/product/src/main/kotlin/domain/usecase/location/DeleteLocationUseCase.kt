package domain.usecase.location

import model.GroupId
import domain.model.LocationId
import domain.repository.LocationRepository
import result.DomainResult

class DeleteLocationUseCase (
    private val repository: LocationRepository
) {
    suspend operator fun invoke(
        locationId: LocationId,
        groupId: GroupId,
    ): DomainResult<Unit> =
        repository.deleteLocation(locationId, groupId)
}