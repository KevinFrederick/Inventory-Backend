package domain.repository

import domain.model.Location
import domain.model.LocationId
import domain.result.DomainResult

interface LocationRepository {
    suspend fun getAllLocation(): DomainResult<List<Location>>
    suspend fun getLocationById(locationId: LocationId): DomainResult<Location?>
    suspend fun insertLocation(location: Location): DomainResult<Unit>
    suspend fun updateLocation(location: Location): DomainResult<Unit>
    suspend fun deleteLocation(locationId: LocationId): DomainResult<Unit>
}