package data.table.product

import org.jetbrains.exposed.v1.core.Table

object DeletedTable: Table("deleted") {
    val entityId = varchar("entity_id", 64)
    val entityType = varchar("entity_type", 64)
    val deletedAt = long("deleted_at")

    override val primaryKey: PrimaryKey = PrimaryKey(entityId, entityType)
}