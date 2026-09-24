package data.repository

import DatabaseFactory.dbQuery
import data.local.table.CategoryTable
import data.local.table.DeletedTable
import data.local.table.LocationTable
import data.local.table.ProductTable
import data.local.table.StockBatchTable
import data.mapper.toProduct
import data.mapper.toStockBatch
import data.util.EntityType
import domain.model.Product
import domain.model.ProductId
import domain.repository.ProductRepository
import result.DomainResult
import result.ErrorType
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

class ProductRepositoryImpl: ProductRepository {
    override suspend fun getProducts(): DomainResult<List<Product>> = dbQuery {
        try {
            val productRows = ProductTable.innerJoin(CategoryTable)
                .selectAll()
                .toList()

            if (productRows.isEmpty()) return@dbQuery DomainResult.Success(emptyList())

            val productIds = productRows.map { it[ProductTable.productId] }

            val batchRows = StockBatchTable.innerJoin(LocationTable)
                .selectAll()
                .where { StockBatchTable.productId inList productIds }
                .toList()

            val batchByProductId = batchRows.groupBy { it[StockBatchTable.productId] }

            val listProduct = productRows.map { row ->
                val pId = row[ProductTable.productId]

                val productBatches = batchByProductId[pId]?.map { it.toStockBatch() } ?: emptyList()
                row.toProduct(productBatches)
            }

            DomainResult.Success(listProduct)
        } catch (e: Exception) {
            DomainResult.Error(e.message ?: "Unknown error", ErrorType.UNKNOWN)
        }
    }

    override suspend fun getProductById(productId: ProductId): DomainResult<Product?> = dbQuery {
        try {
            val idValue = productId.value

            val productRow = ProductTable.innerJoin(CategoryTable)
                .selectAll()
                .where { ProductTable.productId eq idValue }
                .singleOrNull()

            if (productRow == null) return@dbQuery DomainResult.Success(null)

            val batchRows = StockBatchTable.innerJoin(LocationTable)
                .selectAll()
                .where { StockBatchTable.productId eq idValue }
                .toList()

            val productBatches = batchRows.map { it.toStockBatch() }

            val product = productRow.toProduct(productBatches)

            DomainResult.Success(product)
        } catch (e: Exception) {
            DomainResult.Error(e.message ?: "Unknown error", ErrorType.UNKNOWN)
        }
    }

    override suspend fun insertProduct(product: Product): DomainResult<Product> = dbQuery {
        try {
            val timeStamp = System.currentTimeMillis()

            val insertStatement = ProductTable.insert {
                it[productId] = product.productId.value
                it[categoryId] = product.category.categoryId.value
                it[name] = product.name
                it[description] = product.description
                it[barcode] = product.barcode
                it[barcodeFormat] = product.barcodeFormat
                it[sku] = product.sku
                it[imageUri] = product.imageUri
                it[minimumQuantity] = product.minimumQuantity
                it[createdAt] = product.createdAt
                it[lastUpdated] = product.lastUpdated
                it[serverUpdatedAt] = timeStamp
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
                    this[StockBatchTable.serverUpdatedAt] = timeStamp
                }
            }

            DomainResult.Success(product)
        } catch (e: ExposedSQLException) {
            val errorMessage = e.message ?: ""

            when{
                errorMessage.contains("product_sku_unique") ->
                    DomainResult.Error("SKU already exists", ErrorType.CONFLICT)
                errorMessage.contains("product_barcode_unique") ->
                    DomainResult.Error("Barcode already exists", ErrorType.CONFLICT)
                errorMessage.contains("product_category_id_fkey") ->
                    DomainResult.Error("Invalid Category ID", ErrorType.NOT_FOUND)
                else ->
                    DomainResult.Error("Failed to save product", ErrorType.UNKNOWN)
            }
        }
    }

    override suspend fun updateProduct(product: Product): DomainResult<Product> = dbQuery {
        try {
            val updatedRows = ProductTable.update({ ProductTable.productId eq product.productId.value}) {
                it[categoryId] = product.category.categoryId.value
                it[name] = product.name
                it[description] = product.description
                it[barcode] = product.barcode
                it[barcodeFormat] = product.barcodeFormat
                it[sku] = product.sku
                it[imageUri] = product.imageUri
                it[minimumQuantity] = product.minimumQuantity
                it[lastUpdated] = product.lastUpdated
                it[serverUpdatedAt] = System.currentTimeMillis()
            }

            if (updatedRows == 0) {
                return@dbQuery DomainResult.Error("Product Not Found", ErrorType.NOT_FOUND)
            }

            DomainResult.Success(product)
        } catch (e: ExposedSQLException) {
            val errorMessage = e.message ?: ""

            when{
                errorMessage.contains("product_sku_unique") ->
                    DomainResult.Error("SKU already exists", ErrorType.CONFLICT)
                errorMessage.contains("product_barcode_unique") ->
                    DomainResult.Error("Barcode already exists", ErrorType.CONFLICT)
                errorMessage.contains("product_category_id_fkey") ->
                    DomainResult.Error("Invalid Category ID", ErrorType.NOT_FOUND)
                else ->
                    DomainResult.Error("Failed to save product", ErrorType.UNKNOWN)
            }
        }
    }

    override suspend fun updateProductImage(
        productId: ProductId,
        imageUriPath: String,
        updatedAt: Long
    ): DomainResult<Unit> = dbQuery {
        try {
            val updatedRows = ProductTable.update ( { ProductTable.productId eq productId.value }) {
                it[imageUri] = imageUriPath
                it[lastUpdated] = updatedAt
                it[serverUpdatedAt] = System.currentTimeMillis()
            }

            if (updatedRows == 0) return@dbQuery DomainResult.Error("Product not found", ErrorType.NOT_FOUND)

            DomainResult.Success(Unit)
        } catch (e: ExposedSQLException) {
            DomainResult.Error(e.message ?: "Unknown error", ErrorType.UNKNOWN)
        }
    }

    override suspend fun deleteProduct(productId: ProductId): DomainResult<Unit> = dbQuery {
        try {
            val deletedRows = ProductTable.deleteWhere { ProductTable.productId eq productId.value }

            if (deletedRows == 0) {
                return@dbQuery DomainResult.Error("Product Not Found", ErrorType.NOT_FOUND)
            }

            DeletedTable.insert {
                it[entityId] = productId.value
                it[entityType] = EntityType.PRODUCT.name
                it[deletedAt] = System.currentTimeMillis()
            }

            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Error(e.message ?: "Unknown error", ErrorType.UNKNOWN)
        }
    }

    override suspend fun deleteProductImage(
        productId: ProductId,
        updatedAt: Long
    ): DomainResult<Unit> = dbQuery {
        try {
            val updatedRows = ProductTable.update({ ProductTable.productId eq productId.value }) {
                it[imageUri] = null
                it[lastUpdated] = updatedAt
                it[serverUpdatedAt] = System.currentTimeMillis()
            }

            if (updatedRows == 0) return@dbQuery DomainResult.Error("Product Not Found", ErrorType.NOT_FOUND)

            DomainResult.Success(Unit)
        } catch (e: ExposedSQLException) {
            DomainResult.Error(e.message ?: "Unknown error", ErrorType.UNKNOWN)
        }
    }
}