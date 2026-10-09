package data.mapper

import data.table.product.StockBatchTable
import domain.model.BatchId
import domain.model.ProductId
import domain.model.StockBatch
import model.GroupId
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toStockBatch(): StockBatch =
    StockBatch(
        batchId = BatchId(this[StockBatchTable.batchId]),
        productId = ProductId(this[StockBatchTable.productId]),
        groupId = GroupId(this[StockBatchTable.groupId]),
        location = this.toLocation(),
        quantity = this[StockBatchTable.quantity],
        price = this[StockBatchTable.price],
        expirationDate = this[StockBatchTable.expirationDate],
        supplier = this[StockBatchTable.supplier],
        createdAt = this[StockBatchTable.createdAt],
        lastUpdated = this[StockBatchTable.lastUpdated]
    )