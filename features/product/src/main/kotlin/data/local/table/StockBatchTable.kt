package data.local.table

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table

object StockBatchTable: Table("stock_batch") {
    val batchId = varchar("batch_id", 64)

    val productId = reference(
        "product_id",
        ProductTable.productId,
        onDelete = ReferenceOption.CASCADE,
    )

    val locationId = reference(
        "location_id",
        LocationTable.locationId,
        onDelete = ReferenceOption.RESTRICT,
    )

    val quantity = integer("quantity")
    val expirationDate = long("expiration_date").nullable()
    val price = double("price").default(0.0)
    val supplier = text("supplier").nullable()
    val createdAt = long("created_at")
    val lastUpdated = long("last_updated")
}