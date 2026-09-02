package data.repository

import DatabaseFactory.dbQuery
import data.local.table.LocationTable
import data.mapper.toLocation
import domain.model.Location
import domain.model.LocationId
import domain.repository.LocationRepository
import domain.result.DomainResult
import domain.result.ErrorType
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

class LocationRepositoryImpl: LocationRepository {
    override suspend fun getAllLocation(): DomainResult<List<Location>> = dbQuery {
        try {
            val locationRows = LocationTable
                .selectAll()
                .toList()

            val locations = locationRows.map { row ->
                row.toLocation()
            }

            DomainResult.Success(locations)
        } catch (e: Exception) {
            DomainResult.Error(e.message ?: "Unknown error", ErrorType.UNKNOWN)
        }
    }

    override suspend fun getLocationById(locationId: LocationId): DomainResult<Location?> = dbQuery {
        try {
            val locationRow = LocationTable
                .selectAll()
                .where { LocationTable.locationId eq locationId.value }
                .singleOrNull()

            if (locationRow == null) return@dbQuery DomainResult.Success(null)

            val location = locationRow.toLocation()

            DomainResult.Success(location)
        } catch (e: Exception) {
            DomainResult.Error(e.message ?: "Unknown error", ErrorType.UNKNOWN)
        }
    }

    override suspend fun insertLocation(location: Location): DomainResult<Unit> = dbQuery {
        try {
            LocationTable.insert {
                it[locationId] = location.locationId.value
                it[name] = location.name
                it[description] = location.description
                it[locationBarcode] = location.locationBarcode
                it[createdAt] = location.createdAt
                it[lastUpdated] = location.lastUpdated
            }

            DomainResult.Success(Unit)
        } catch (e: ExposedSQLException) {
            val errorMessage = e.message ?: ""

            when{
                errorMessage.contains("location_name_key") ->
                    DomainResult.Error("Location already exists", ErrorType.CONFLICT)
                else ->
                    DomainResult.Error("Failed to saved location", ErrorType.UNKNOWN)
            }
        }
    }

    override suspend fun updateLocation(location: Location): DomainResult<Unit> = dbQuery {
        try {
            val updatedRowsCount = LocationTable.update({ LocationTable.locationId eq location.locationId.value }) {
                it[name] = location.name
                it[description] = location.description
                it[locationBarcode] = location.locationBarcode
                it[lastUpdated] = location.lastUpdated
            }

            if (updatedRowsCount == 0) return@dbQuery DomainResult.Error("Location not found", ErrorType.NOT_FOUND)

            DomainResult.Success(Unit)
        } catch (e: ExposedSQLException) {
            val errorMessage = e.message ?: ""

            when{
                errorMessage.contains("location_name_key") ->
                    DomainResult.Error("Location already exists", ErrorType.CONFLICT)
                else ->
                    DomainResult.Error("Failed to saved location", ErrorType.UNKNOWN)
            }
        }
    }

    override suspend fun deleteLocation(locationId: LocationId): DomainResult<Unit> = dbQuery {
        try {
            val deletedRowsCount = LocationTable.deleteWhere { LocationTable.locationId eq locationId.value }

            if (deletedRowsCount == 0) return@dbQuery DomainResult.Error("Location not found", ErrorType.NOT_FOUND)

            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Error(e.message ?: "Unknown error", ErrorType.UNKNOWN)
        }
    }
}