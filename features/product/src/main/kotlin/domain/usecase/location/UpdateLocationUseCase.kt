package domain.usecase.location

import domain.model.Location
import domain.model.LocationId
import domain.repository.LocationRepository
import domain.validation.LocationValidator
import result.DomainResult
import result.ErrorType

class UpdateLocationUseCase (
    private val repository: LocationRepository,
    private val locationValidator: LocationValidator
) {
    suspend operator fun invoke(
        locationId: LocationId,
        location: Location
    ): DomainResult<Unit> {
        return if (location.locationId != locationId) {
            DomainResult.Error("Location Id doesn't match", ErrorType.BAD_REQUEST)
        } else {
            when(
                val validationResult = locationValidator.validateLocation(location)
            ) {
                is DomainResult.Error -> return validationResult
                is DomainResult.Success -> {}
            }

            repository.updateLocation(location)
        }
    }
}