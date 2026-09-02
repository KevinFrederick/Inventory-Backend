package domain.usecase.location

import domain.model.Location
import domain.model.LocationId
import domain.repository.LocationRepository
import domain.result.DomainResult
import domain.result.ErrorType

class GetLocationByIdUseCase (
    private val repository: LocationRepository
) {
    suspend operator fun invoke(locationId: LocationId): DomainResult<Location> {
        return when (
            val result = repository.getLocationById(locationId)
        ) {
            is DomainResult.Success -> {
                if (result.data == null) {
                    DomainResult.Error("Location not found", ErrorType.NOT_FOUND)
                } else {
                    DomainResult.Success(result.data)
                }
            }
            is DomainResult.Error -> result
        }
    }
}