package domain.usecase.location

import model.GroupId
import domain.model.Location
import domain.repository.LocationRepository
import result.DomainResult

class GetAllLocationUseCase (
    private val repository: LocationRepository
) {
    suspend operator fun invoke(groupId: GroupId): DomainResult<List<Location>> =
        repository.getAllLocation(groupId)
}