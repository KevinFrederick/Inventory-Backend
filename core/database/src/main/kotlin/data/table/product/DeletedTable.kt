package data.table.product

import data.table.user.GroupTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table

object DeletedTable: Table("deleted") {
    val entityId = varchar("entity_id", 64)
    val groupId = reference(
        name = "group_id",
        refColumn = GroupTable.groupId,
        onDelete = ReferenceOption.CASCADE
    )
    val entityType = varchar("entity_type", 64)
    val deletedAt = long("deleted_at")

    override val primaryKey: PrimaryKey = PrimaryKey(entityId, entityType)
}