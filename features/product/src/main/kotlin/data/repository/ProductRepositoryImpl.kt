package data.repository

import DatabaseFactory.dbQuery
import data.local.table.CategoryTable
import data.local.table.LocationTable
import data.local.table.ProductTable
import data.local.table.StockBatchTable
import data.mapper.toProduct
import data.mapper.toStockBatch
import domain.model.Product
import domain.model.ProductId
import domain.repository.ProductRepository
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

class ProductRepositoryImpl: ProductRepository {
    override suspend fun getProducts(): List<Product> = dbQuery {
        val productRows = ProductTable.innerJoin(CategoryTable)
            .selectAll()
            .toList()

        if (productRows.isEmpty()) return@dbQuery emptyList()

        val productIds = productRows.map { it[ProductTable.productId] }

        val batchRows = StockBatchTable.innerJoin(LocationTable)
            .selectAll()
            .where { StockBatchTable.productId inList productIds }
            .toList()

        val batchByProductId = batchRows.groupBy { it[StockBatchTable.productId] }

        productRows.map { row ->
            val pId = row[ProductTable.productId]

            val productBatches = batchByProductId[pId]?.map { it.toStockBatch() } ?: emptyList()
            row.toProduct(productBatches)
        }
    }

    override suspend fun getProductById(productId: ProductId): Product? = dbQuery {
        val idValue = productId.value

        val productRow = ProductTable.innerJoin(CategoryTable)
            .selectAll()
            .where { ProductTable.productId eq idValue }
            .singleOrNull()

        if (productRow == null) return@dbQuery null

        val batchRows = StockBatchTable.innerJoin(LocationTable)
            .selectAll()
            .where { StockBatchTable.productId eq idValue }
            .toList()

        val productBatches = batchRows.map { it.toStockBatch() }

        productRow.toProduct(productBatches)
    }

    override suspend fun insertProduct(product: Product): Boolean = dbQuery {
        try {
            val insertStatement = ProductTable.insert {
                it[productId] = product.productId.value
                it[categoryId] = product.category.categoryId.value
                it[name] = product.name
                it[description] = product.description
                it[barcode] = product.barcode
                it[sku] = product.sku
                it[imageUri] = product.imageUri
                it[minimumQuantity] = product.minimumQuantity
                it[createdAt] = product.createdAt
                it[lastUpdated] = product.lastUpdated
            }

            val isProductInserted = insertStatement.insertedCount > 0

            if (isProductInserted && product.batches.isNotEmpty()) {
                StockBatchTable.batchInsert(product.batches) { batch ->
                    this[StockBatchTable.batchId] = batch.batchId.value
                    this[StockBatchTable.productId] = batch.productId.value
                    this[StockBatchTable.locationId] = batch.location.locationId.value
                    this[StockBatchTable.quantity] = batch.quantity
                    this[StockBatchTable.price] = batch.price
                    this[StockBatchTable.expirationDate] = batch.expirationDate
                    this[StockBatchTable.supplier] = batch.supplier
                    this[StockBatchTable.lastUpdated] = batch.lastUpdated
                }
            }

            isProductInserted
        } catch (_: ExposedSQLException) {
            false
        }
    }

    override suspend fun updateProduct(product: Product): Boolean = dbQuery {
        val updatedRowsCount = ProductTable.update({ ProductTable.productId eq product.productId.value}) {
            it[categoryId] = product.category.categoryId.value
            it[name] = product.name
            it[description] = product.description
            it[barcode] = product.barcode
            it[sku] = product.sku
            it[imageUri] = product.imageUri
            it[minimumQuantity] = product.minimumQuantity
            it[lastUpdated] = product.lastUpdated
        }

        updatedRowsCount > 0
    }

    override suspend fun deleteProduct(productId: ProductId): Boolean = dbQuery {
        val deletedRowsCount = ProductTable.deleteWhere { ProductTable.productId eq productId.value }

        deletedRowsCount > 0
    }
}