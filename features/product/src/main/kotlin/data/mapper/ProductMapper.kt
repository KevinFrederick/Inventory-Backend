package data.mapper

import data.table.product.ProductTable
import domain.model.Product
import domain.model.ProductId
import domain.model.StockBatch
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toProduct(batches: List<StockBatch>): Product =
    Product(
        productId = ProductId(this[ProductTable.productId]),
        name = this[ProductTable.name],
        description = this[ProductTable.description],
        category = this.toCategory(),
        barcode = this[ProductTable.barcode],
        barcodeFormat = this[ProductTable.barcodeFormat],
        sku = this[ProductTable.sku],
        imageUri = this[ProductTable.imageUri],
        minimumQuantity = this[ProductTable.minimumQuantity],
        batches = batches,
        createdAt = this[ProductTable.createdAt],
        lastUpdated = this[ProductTable.lastUpdated],
    )
