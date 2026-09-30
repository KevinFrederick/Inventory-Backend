package data.table.auth

import org.jetbrains.exposed.v1.core.Table

object GroupTable: Table("groups") {
    val groupId = varchar("group_id", 64)
    val name = varchar("group_name", 255)
    val description = text("description").nullable()
    val createdAt = long("created_at")
    val lastUpdated = long("last_updated")
    val serverUpdatedAt = long("server_updated_at")

    override val primaryKey: PrimaryKey = PrimaryKey(groupId)
}