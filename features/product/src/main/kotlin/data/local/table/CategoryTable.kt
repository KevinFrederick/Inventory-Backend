package data.local.table

import org.jetbrains.exposed.v1.core.Table

object CategoryTable: Table("category") {
    val categoryId = varchar("category_id", 64)
    val name = varchar("name", 255).uniqueIndex()
    val description = text("description").nullable()
    val createdAt = long("created_at")
    val lastUpdated = long("last_updated")

    override val primaryKey: PrimaryKey = PrimaryKey(categoryId)
}