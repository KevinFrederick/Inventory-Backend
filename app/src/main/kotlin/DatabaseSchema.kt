package com.kevinfreyap

import data.local.table.CategoryTable
import data.local.table.DeletedTable
import data.local.table.LocationTable
import data.local.table.ProductTable
import data.local.table.StockBatchTable
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun initializeDatabaseSchema() {
    transaction {
        SchemaUtils.create(
            CategoryTable,
            LocationTable,

            ProductTable,
            StockBatchTable,
            DeletedTable
        )
    }
}