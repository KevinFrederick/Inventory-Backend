package data.repository

import DatabaseFactory.dbQuery
import data.table.product.CategoryTable
import data.table.product.DeletedTable
import data.table.product.LocationTable
import data.table.product.ProductTable
import data.table.product.StockBatchTable
import data.mapper.toCategory
import data.mapper.toLocation
import data.mapper.toSyncProduct
import data.mapper.toSyncStockBatch
import data.util.EntityType
import domain.model.BatchId
import domain.model.CategoryId
import domain.model.LocationId
import domain.model.ProductId
import domain.model.sync.SyncPayload
import domain.model.sync.SyncPullResponse
import domain.model.sync.SyncPushResponse
import domain.repository.SyncRepository
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.batchUpsert
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.selectAll
import result.DomainResult
import result.ErrorType

class SyncRepositoryImpl: SyncRepository {
    override suspend fun pushSync(syncPayload: SyncPayload): DomainResult<SyncPushResponse> {
        return try {
            val response = dbQuery {
                val timeStamp = System.currentTimeMillis()

                val deleted = mutableListOf<Pair<String, String>>()
                syncPayload.deletedBatches.forEach { deleted.add(it.value to EntityType.BATCH.name) }
                syncPayload.deletedProduct.forEach { deleted.add(it.value to EntityType.PRODUCT.name) }
                syncPayload.deletedLocations.forEach { deleted.add(it.value to EntityType.LOCATION.name) }
                syncPayload.deletedCategories.forEach { deleted.add(it.value to EntityType.CATEGORY.name) }

                if (deleted.isNotEmpty()) {
                    DeletedTable.batchUpsert(deleted) { (id, type) ->
                        this[DeletedTable.entityId] = id
                        this[DeletedTable.entityType] = type
                        this[DeletedTable.deletedAt] = timeStamp
                    }
                }

                // Delete Bottom - Up
                if (syncPayload.deletedBatches.isNotEmpty()) {
                    StockBatchTable.deleteWhere { StockBatchTable.batchId inList syncPayload.deletedBatches.map { it.value } }
                }

                if (syncPayload.deletedProduct.isNotEmpty()){
                    ProductTable.deleteWhere { ProductTable.productId inList syncPayload.deletedProduct.map { it.value } }
                }

                if (syncPayload.deletedLocations.isNotEmpty()) {
                    LocationTable.deleteWhere { LocationTable.locationId inList syncPayload.deletedLocations.map { it.value } }
                }

                if (syncPayload.deletedCategories.isNotEmpty()) {
                    CategoryTable.deleteWhere { CategoryTable.categoryId inList syncPayload.deletedCategories.map { it.value } }
                }

                // Conflict Resolution
                val allCategories = (syncPayload.createdCategories + syncPayload.updatedCategories)
                    .sortedByDescending { it.lastUpdated }
                    .distinctBy { it.categoryId.value }
                val allLocations = (syncPayload.createdLocations + syncPayload.updatedLocations)
                    .sortedByDescending { it.lastUpdated }
                    .distinctBy { it.locationId.value }
                val allProducts = (syncPayload.createdProduct + syncPayload.updatedProduct)
                    .sortedByDescending { it.lastUpdated }
                    .distinctBy { it.productId.value }
                val allBatches = (syncPayload.createdBatches + syncPayload.updatedBatches)
                    .sortedByDescending { it.lastUpdated }
                    .distinctBy { it.batchId.value }

                val incomingIds = allCategories.map { it.categoryId.value } +
                                  allLocations.map { it.locationId.value } +
                                  allProducts.map { it.productId.value } +
                                  allBatches.map { it.batchId.value }

                val deletedMap = mutableMapOf<String, Long>()
                if (incomingIds.isNotEmpty()) {
                    DeletedTable
                        .selectAll()
                        .where { DeletedTable.entityId inList incomingIds }
                        .forEach { row ->
                            deletedMap[row[DeletedTable.entityId]] = row[DeletedTable.deletedAt]
                        }
                }

                // Create & Update Top - Down
                val categories = allCategories.filter { category ->
                    category.lastUpdated > (deletedMap[category.categoryId.value] ?: 0L)
                }
                if (categories.isNotEmpty()) {
                    CategoryTable.batchUpsert(
                        data = categories,
                        keys = arrayOf(CategoryTable.categoryId)
                    ) { category ->
                        this[CategoryTable.categoryId] = category.categoryId.value
                        this[CategoryTable.name] = category.name
                        this[CategoryTable.description] = category.description
                        this[CategoryTable.createdAt] = category.createdAt
                        this[CategoryTable.lastUpdated] = category.lastUpdated
                        this[CategoryTable.serverUpdatedAt] = timeStamp
                    }
                }

                val locations = allLocations.filter { location ->
                    location.lastUpdated > (deletedMap[location.locationId.value] ?: 0L)
                }
                if (locations.isNotEmpty()) {
                    LocationTable.batchUpsert(
                        data = locations,
                        keys = arrayOf(LocationTable.locationId)
                    ) { location ->
                        this[LocationTable.locationId] = location.locationId.value
                        this[LocationTable.name] = location.name
                        this[LocationTable.description] = location.description
                        this[LocationTable.locationBarcode] = location.locationBarcode
                        this[LocationTable.createdAt] = location.createdAt
                        this[LocationTable.lastUpdated] = location.lastUpdated
                        this[LocationTable.serverUpdatedAt] = timeStamp
                    }
                }

                val products = allProducts.filter { product ->
                    product.lastUpdated > (deletedMap[product.productId.value] ?: 0L)
                }
                if (products.isNotEmpty()) {
                    ProductTable.batchUpsert(
                        data = products,
                        keys = arrayOf(ProductTable.productId),
                    ) { product ->
                        this[ProductTable.productId] = product.productId.value
                        this[ProductTable.categoryId] = product.categoryId.value
                        this[ProductTable.name] = product.name
                        this[ProductTable.description] = product.description
                        this[ProductTable.barcode] = product.barcode
                        this[ProductTable.barcodeFormat] = product.barcodeFormat
                        this[ProductTable.sku] = product.sku
                        this[ProductTable.imageUri] = product.imageUri
                        this[ProductTable.minimumQuantity] = product.minimumQuantity
                        this[ProductTable.createdAt] = product.createdAt
                        this[ProductTable.lastUpdated] = product.lastUpdated
                        this[ProductTable.serverUpdatedAt] = timeStamp
                    }
                }

                val batches = allBatches.filter { batch ->
                    batch.lastUpdated > (deletedMap[batch.batchId.value] ?: 0L)
                }
                if(batches.isNotEmpty()) {
                    StockBatchTable.batchUpsert(
                        data = batches,
                        keys = arrayOf(StockBatchTable.batchId),
                    ) { batch ->
                        this[StockBatchTable.batchId] = batch.batchId.value
                        this[StockBatchTable.productId] = batch.productId.value
                        this[StockBatchTable.locationId] = batch.locationId.value
                        this[StockBatchTable.quantity] = batch.quantity
                        this[StockBatchTable.price] = batch.price
                        this[StockBatchTable.expirationDate] = batch.expirationDate
                        this[StockBatchTable.supplier] = batch.supplier
                        this[StockBatchTable.createdAt] = batch.createdAt
                        this[StockBatchTable.lastUpdated] = batch.lastUpdated
                        this[StockBatchTable.serverUpdatedAt] = timeStamp
                    }
                }

                SyncPushResponse(
                    success = true,
                    message = "Sync Success",
                    serverTimeStamp = timeStamp
                )
            }

            DomainResult.Success(response)
        } catch (e: Exception) {
            DomainResult.Error(e.message ?: "Failed to push sync data", ErrorType.UNKNOWN)
        }
    }

    override suspend fun pullSync(updatedAfter: Long): DomainResult<SyncPullResponse> {
        return try {
            val response = dbQuery {
                val timeStamp = System.currentTimeMillis()

                val updatedCategories = CategoryTable
                    .selectAll()
                    .where { CategoryTable.serverUpdatedAt greater updatedAfter }
                    .map { it.toCategory() }

                val updatedLocations = LocationTable
                    .selectAll()
                    .where { LocationTable.serverUpdatedAt greater updatedAfter }
                    .map { it.toLocation() }

                val updatedProduct = ProductTable
                    .selectAll()
                    .where { ProductTable.serverUpdatedAt greater updatedAfter }
                    .map { it.toSyncProduct() }

                val updatedBatch = StockBatchTable
                    .selectAll()
                    .where { StockBatchTable.serverUpdatedAt greater updatedAfter }
                    .map { it.toSyncStockBatch() }

                val deleted = DeletedTable
                    .selectAll()
                    .where { DeletedTable.deletedAt greater updatedAfter }
                    .toList()

                val deletedCategoryIds = deleted.filter { it[DeletedTable.entityType] == EntityType.CATEGORY.name }
                    .map { CategoryId(it[DeletedTable.entityId]) }
                val deletedLocationIds = deleted.filter { it[DeletedTable.entityType] == EntityType.LOCATION.name }
                    .map { LocationId(it[DeletedTable.entityId]) }
                val deletedProductIds = deleted.filter { it[DeletedTable.entityType] == EntityType.PRODUCT.name }
                    .map { ProductId(it[DeletedTable.entityId]) }
                val deletedBatchIds = deleted.filter { it[DeletedTable.entityType] == EntityType.BATCH.name }
                    .map { BatchId(it[DeletedTable.entityId]) }

                SyncPullResponse(
                    categories = updatedCategories,
                    locations = updatedLocations,
                    products = updatedProduct,
                    batches = updatedBatch,
                    deletedCategories = deletedCategoryIds,
                    deletedLocations = deletedLocationIds,
                    deletedProducts = deletedProductIds,
                    deletedBatches = deletedBatchIds,
                    serverTimeStamp = timeStamp
                )
            }

            DomainResult.Success(response)
        } catch (e: Exception) {
            DomainResult.Error(e.message ?: "Failed to pull sync data", ErrorType.UNKNOWN)
        }
    }
}