package domain.usecase.location

import domain.model.Location
import domain.repository.LocationRepository
import domain.result.DomainResult

class InsertLocationUseCase (
    private val repository: LocationRepository
) {
    suspend operator fun invoke(location: Location): DomainResult<Unit> =
        repository.insertLocation(location)
}