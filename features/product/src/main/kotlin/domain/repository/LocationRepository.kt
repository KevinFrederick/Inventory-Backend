package domain.repository

import domain.model.Location
import domain.model.LocationId

interface LocationRepository {
    suspend fun getAllLocation(): List<Location>
    suspend fun getLocationById(locationId: LocationId): Location?
    suspend fun insertLocation(location: Location): Boolean
    suspend fun updateLocation(location: Location): Boolean
    suspend fun deleteLocation(locationId: LocationId): Boolean
}