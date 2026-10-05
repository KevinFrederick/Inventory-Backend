package data.table.product

import data.table.user.GroupTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table

object LocationTable: Table("location") {
    val locationId = varchar("location_id", 64)
    val groupId = reference(
        name = "group_id",
        refColumn = GroupTable.groupId,
        onDelete = ReferenceOption.CASCADE
    )
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val locationBarcode = varchar("location_barcode", 64).nullable()
    val createdAt = long("created_at")
    val lastUpdated = long("last_updated")
    val serverUpdatedAt = long("server_updated_at")

    override val primaryKey: PrimaryKey = PrimaryKey(locationId)

    init {
        uniqueIndex(groupId, name)
    }
}