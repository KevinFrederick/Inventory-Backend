package domain.repository

import domain.model.Location
import domain.model.LocationId
import model.GroupId
import result.DomainResult

interface LocationRepository {
    suspend fun getAllLocation(groupId: GroupId): DomainResult<List<Location>>
    suspend fun getLocationById(locationId: LocationId, groupId: GroupId): DomainResult<Location>
    suspend fun getLocationsByIds(locationIds: List<LocationId>, groupId: GroupId): DomainResult<List<Location>>
    suspend fun insertLocation(location: Location, groupId: GroupId): DomainResult<Unit>
    suspend fun updateLocation(location: Location, groupId: GroupId): DomainResult<Unit>
    suspend fun deleteLocation(locationId: LocationId, groupId: GroupId): DomainResult<Unit>
}