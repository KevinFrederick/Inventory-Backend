package data.repository

import DatabaseFactory.dbQuery
import data.local.table.LocationTable
import data.mapper.toLocation
import domain.model.Location
import domain.model.LocationId
import domain.repository.LocationRepository
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

class LocationRepositoryImpl: LocationRepository {
    override suspend fun getAllLocation(): List<Location> = dbQuery {
        val locationRows = LocationTable
            .selectAll()
            .toList()

        locationRows.map { row ->
            row.toLocation()
        }
    }

    override suspend fun getLocationById(locationId: LocationId): Location? = dbQuery {
        val locationRow = LocationTable
            .selectAll()
            .where { LocationTable.locationId eq locationId.value }
            .singleOrNull()

        if (locationRow == null) return@dbQuery null

        locationRow.toLocation()
    }

    override suspend fun insertLocation(location: Location): Boolean = dbQuery {
        try {
            val insertStatement = LocationTable.insert {
                it[locationId] = location.locationId.value
                it[name] = location.name
                it[description] = location.description
                it[locationBarcode] = location.locationBarcode
                it[createdAt] = location.createdAt
                it[lastUpdated] = location.lastUpdated
            }

            insertStatement.insertedCount > 0
        } catch (_: ExposedSQLException) {
            false
        }
    }

    override suspend fun updateLocation(location: Location): Boolean = dbQuery {
        val updatedRowsCount = LocationTable.update({ LocationTable.locationId eq location.locationId.value }) {
            it[name] = location.name
            it[description] = location.description
            it[locationBarcode] = location.locationBarcode
            it[lastUpdated] = location.lastUpdated
        }

        updatedRowsCount > 0
    }

    override suspend fun deleteLocation(locationId: LocationId): Boolean = dbQuery {
        val deletedRowsCount = LocationTable.deleteWhere { LocationTable.locationId eq locationId.value }

        deletedRowsCount > 0
    }
}