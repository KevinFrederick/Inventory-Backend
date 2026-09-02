package domain.usecase.location

import domain.model.Location
import domain.model.LocationId
import domain.repository.LocationRepository
import domain.result.DomainResult
import domain.result.ErrorType

class UpdateLocationUseCase (
    private val repository: LocationRepository
) {
    suspend operator fun invoke(
        locationId: LocationId,
        location: Location
    ): DomainResult<Unit> {
        return if (location.locationId != locationId) {
            DomainResult.Error("Location Id doesn't match", ErrorType.BAD_REQUEST)
        } else {
            repository.updateLocation(location)
        }
    }
}