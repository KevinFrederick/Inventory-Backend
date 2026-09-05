package domain.usecase.location

import domain.model.Location
import domain.model.LocationId
import domain.repository.LocationRepository
import result.DomainResult
import result.ErrorType

class GetLocationByIdUseCase (
    private val repository: LocationRepository
) {
    suspend operator fun invoke(locationId: LocationId): DomainResult<Location> {
        return when (
            val result = repository.getLocationById(locationId)
        ) {
            is DomainResult.Success -> {
                val location = result.data

                if (location == null) {
                    DomainResult.Error("Location not found", ErrorType.NOT_FOUND)
                } else {
                    DomainResult.Success(location)
                }
            }
            is DomainResult.Error -> result
        }
    }
}