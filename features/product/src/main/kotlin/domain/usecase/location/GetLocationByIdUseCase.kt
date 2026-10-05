package domain.usecase.location

import model.GroupId
import domain.model.Location
import domain.model.LocationId
import domain.repository.LocationRepository
import result.DomainResult

class GetLocationByIdUseCase (
    private val repository: LocationRepository
) {
    suspend operator fun invoke(
        locationId: LocationId,
        groupId: GroupId
    ): DomainResult<Location> =
        repository.getLocationById(locationId, groupId)
}