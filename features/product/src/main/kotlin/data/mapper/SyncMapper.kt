package data.mapper

import data.local.table.ProductTable
import data.local.table.StockBatchTable
import domain.model.BatchId
import domain.model.CategoryId
import domain.model.LocationId
import domain.model.ProductId
import domain.model.sync.SyncProduct
import domain.model.sync.SyncStockBatch
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toSyncProduct(): SyncProduct =
    SyncProduct(
        productId = ProductId(this[ProductTable.productId]),
        categoryId = CategoryId(this[ProductTable.categoryId]),
        name = this[ProductTable.name],
        description = this[ProductTable.description],
        barcode = this[ProductTable.barcode],
        sku = this[ProductTable.sku],
        imageUri = this[ProductTable.imageUri],
        minimumQuantity = this[ProductTable.minimumQuantity],
        createdAt = this[ProductTable.createdAt],
        lastUpdated = this[ProductTable.lastUpdated]
    )

fun ResultRow.toSyncStockBatch(): SyncStockBatch =
    SyncStockBatch(
        batchId = BatchId(this[StockBatchTable.batchId]),
        productId = ProductId(this[StockBatchTable.productId]),
        locationId = LocationId(this[StockBatchTable.locationId]),
        quantity = this[StockBatchTable.quantity],
        price = this[StockBatchTable.price],
        expirationDate = this[StockBatchTable.expirationDate],
        supplier = this[StockBatchTable.supplier],
        createdAt = this[StockBatchTable.createdAt],
        lastUpdated = this[StockBatchTable.lastUpdated]
    )