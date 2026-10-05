package data.repository

import DatabaseFactory.dbQuery
import data.table.product.DeletedTable
import data.table.product.LocationTable
import data.mapper.toLocation
import data.util.EntityType
import domain.model.Location
import domain.model.LocationId
import domain.repository.LocationRepository
import model.GroupId
import org.jetbrains.exposed.v1.core.and
import result.DomainResult
import result.ErrorType
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import org.slf4j.LoggerFactory

class LocationRepositoryImpl: LocationRepository {
    private val logger = LoggerFactory.getLogger(LocationRepositoryImpl::class.java)

    override suspend fun getAllLocation(groupId: GroupId): DomainResult<List<Location>> = dbQuery {
        try {
            val locationRows = LocationTable
                .selectAll()
                .where { LocationTable.groupId eq groupId.value }
                .toList()

            val locations = locationRows.map { row ->
                row.toLocation()
            }

            DomainResult.Success(locations)
        } catch (e: Exception) {
            logger.error("Error while fetching locations", e)
            DomainResult.Error(e.message ?: "Unknown error", ErrorType.UNKNOWN)
        }
    }

    override suspend fun getLocationById(locationId: LocationId, groupId: GroupId): DomainResult<Location> = dbQuery {
        try {
            val locationRow = LocationTable
                .selectAll()
                .where { (LocationTable.locationId eq locationId.value) and (LocationTable.groupId eq groupId.value) }
                .singleOrNull()

            if (locationRow == null) return@dbQuery DomainResult.Error("Location not found", ErrorType.NOT_FOUND)

            val location = locationRow.toLocation()

            DomainResult.Success(location)
        } catch (e: Exception) {
            logger.error("Error while fetching location", e)
            DomainResult.Error(e.message ?: "Unknown error", ErrorType.UNKNOWN)
        }
    }

    override suspend fun getLocationsByIds(
        locationIds: List<LocationId>,
        groupId: GroupId
    ): DomainResult<List<Location>> = dbQuery {
        try {
            val idValues = locationIds.map { it.value }

            val locationRows = LocationTable
                .selectAll()
                .where { (LocationTable.locationId inList idValues) and (LocationTable.groupId eq groupId.value) }
                .toList()

            val locations = locationRows.map { it.toLocation() }

            DomainResult.Success(locations)
        } catch (e: Exception) {
            logger.error("Error while fetching locations", e)
            DomainResult.Error(e.message ?: "Failed to fetch locations", ErrorType.UNKNOWN)
        }
    }

    override suspend fun insertLocation(location: Location, groupId: GroupId): DomainResult<Unit> = dbQuery {
        try {
            LocationTable.insert {
                it[locationId] = location.locationId.value
                it[this.groupId] = groupId.value
                it[name] = location.name
                it[description] = location.description
                it[locationBarcode] = location.locationBarcode
                it[createdAt] = location.createdAt
                it[lastUpdated] = location.lastUpdated
                it[serverUpdatedAt] = System.currentTimeMillis()
            }

            DomainResult.Success(Unit)
        } catch (e: ExposedSQLException) {
            logger.error("Error while inserting location", e)
            val errorMessage = e.message ?: ""

            when{
                errorMessage.contains("location_name_unique") ->
                    DomainResult.Error("Location already exists", ErrorType.CONFLICT)
                else ->
                    DomainResult.Error("Failed to saved location", ErrorType.UNKNOWN)
            }
        }
    }

    override suspend fun updateLocation(location: Location, groupId: GroupId): DomainResult<Unit> = dbQuery {
        try {
            val updatedRowsCount = LocationTable.update({
                (LocationTable.locationId eq location.locationId.value) and (LocationTable.groupId eq groupId.value)
            }) {
                it[name] = location.name
                it[description] = location.description
                it[locationBarcode] = location.locationBarcode
                it[lastUpdated] = location.lastUpdated
                it[serverUpdatedAt] = System.currentTimeMillis()
            }

            if (updatedRowsCount == 0) return@dbQuery DomainResult.Error("Location not found", ErrorType.NOT_FOUND)

            DomainResult.Success(Unit)
        } catch (e: ExposedSQLException) {
            logger.error("Error while updating location", e)
            val errorMessage = e.message ?: ""

            when{
                errorMessage.contains("location_name_unique") ->
                    DomainResult.Error("Location already exists", ErrorType.CONFLICT)
                else ->
                    DomainResult.Error("Failed to saved location", ErrorType.UNKNOWN)
            }
        }
    }

    override suspend fun deleteLocation(locationId: LocationId, groupId: GroupId): DomainResult<Unit> = dbQuery {
        try {
            val deletedRowsCount = LocationTable.deleteWhere {
                (LocationTable.locationId eq locationId.value) and (LocationTable.groupId eq groupId.value)
            }

            if (deletedRowsCount == 0) return@dbQuery DomainResult.Error("Location not found", ErrorType.NOT_FOUND)

            DeletedTable.insert {
                it[entityId] = locationId.value
                it[this.groupId] = groupId.value
                it[entityType] = EntityType.LOCATION.name
                it[deletedAt] = System.currentTimeMillis()
            }

            DomainResult.Success(Unit)
        } catch (e: Exception) {
            logger.error("Error while deleting location", e)
            DomainResult.Error(e.message ?: "Unknown error", ErrorType.UNKNOWN)
        }
    }
}