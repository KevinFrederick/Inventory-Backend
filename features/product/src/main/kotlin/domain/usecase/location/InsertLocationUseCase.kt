package domain.usecase.location

import domain.model.Location
import domain.repository.LocationRepository
import domain.validation.LocationValidator
import result.DomainResult

class InsertLocationUseCase (
    private val repository: LocationRepository,
    private val locationValidator: LocationValidator
) {
    suspend operator fun invoke(location: Location): DomainResult<Unit> {
        when(
            val validationResult = locationValidator.validateLocation(location)
        ) {
            is DomainResult.Error -> return validationResult
            is DomainResult.Success -> {}
        }

        return repository.insertLocation(location)
    }
}