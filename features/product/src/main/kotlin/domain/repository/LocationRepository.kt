package domain.repository

import domain.model.Location
import domain.model.LocationId
import result.DomainResult

interface LocationRepository {
    suspend fun getAllLocation(): DomainResult<List<Location>>
    suspend fun getLocationById(locationId: LocationId): DomainResult<Location?>
    suspend fun getLocationsByIds(locationIds: List<LocationId>): DomainResult<List<Location>>
    suspend fun insertLocation(location: Location): DomainResult<Unit>
    suspend fun updateLocation(location: Location): DomainResult<Unit>
    suspend fun deleteLocation(locationId: LocationId): DomainResult<Unit>
}