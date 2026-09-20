package data.local.table

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table

object ProductTable: Table("product") {
    val productId = varchar("product_id", 64)

    val categoryId = reference(
        "category_id",
        CategoryTable.categoryId,
        ReferenceOption.RESTRICT
    )

    val name = varchar("name", 255)
    val description = text("description").nullable()
    val barcode = varchar("barcode", 64).nullable()
    val sku = varchar("sku", 128).uniqueIndex().nullable()
    val imageUri = text("image_uri").nullable()
    val minimumQuantity = integer("minimum_quantity").default(0)
    val createdAt = long("created_at")
    val lastUpdated = long("last_updated")
    val serverUpdatedAt = long("server_updated_at")

    override val primaryKey: PrimaryKey = PrimaryKey(productId)
}