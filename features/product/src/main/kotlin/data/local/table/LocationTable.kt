package data.local.table

import org.jetbrains.exposed.v1.core.Table

object LocationTable: Table("location") {
    val locationId = varchar("location_id", 64)
    val name = varchar("name", 255).uniqueIndex()
    val description = text("description").nullable()
    val locationBarcode = varchar("location_barcode", 64).nullable()
    val createdAt = long("created_at")
    val lastUpdated = long("last_updated")
    val serverUpdatedAt = long("server_updated_at")

    override val primaryKey: PrimaryKey = PrimaryKey(locationId)
}