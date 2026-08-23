package com.kevinfreyap

import data.local.table.CategoryTable
import data.local.table.LocationTable
import data.local.table.ProductTable
import data.local.table.StockBatchTable
import data.local.table.TransactionItemTable
import data.local.table.TransactionTable
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun initializeDatabaseSchema() {
    transaction {
        SchemaUtils.create(
            CategoryTable,
            LocationTable,
            TransactionTable,

            ProductTable,
            StockBatchTable,
            TransactionItemTable
        )
    }
}