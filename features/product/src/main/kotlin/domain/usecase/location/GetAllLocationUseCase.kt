package domain.usecase.location

import domain.model.Location
import domain.repository.LocationRepository
import domain.result.DomainResult

class GetAllLocationUseCase (
    private val repository: LocationRepository
) {
    suspend operator fun invoke(): DomainResult<List<Location>> =
        repository.getAllLocation()
}