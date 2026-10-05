package domain.usecase.sync

import model.GroupId
import domain.model.sync.SyncPayload
import domain.model.sync.SyncPushResponse
import domain.repository.SyncRepository
import domain.validation.CategoryValidator
import domain.validation.LocationValidator
import domain.validation.ProductValidator
import domain.validation.StockBatchValidator
import result.DomainResult

class SyncPushUseCase (
    private val repository: SyncRepository,
    private val categoryValidator: CategoryValidator,
    private val locationValidator: LocationValidator,
    private val productValidator: ProductValidator,
    private val stockBatchValidator: StockBatchValidator
) {
    suspend operator fun invoke(
        syncPayload: SyncPayload,
        groupId: GroupId,
    ): DomainResult<SyncPushResponse> {
        val allCategories = syncPayload.createdCategories + syncPayload.updatedCategories
        val allLocations = syncPayload.createdLocations + syncPayload.updatedLocations
        val allProducts = syncPayload.createdProduct + syncPayload.updatedProduct
        val allBatches = syncPayload.createdBatches + syncPayload.updatedBatches

        allCategories.forEach { category ->
            when(
                val validationResult = categoryValidator.validateCategory(category)
            ) {
                is DomainResult.Error -> return validationResult
                is DomainResult.Success -> {}
            }
        }

        allLocations.forEach { location ->
            when(
                val validationResult = locationValidator.validateLocation(location)
            ) {
                is DomainResult.Error -> return validationResult
                is DomainResult.Success -> {}
            }
        }

        allProducts.forEach { product ->
            when(
                val validationResult = productValidator.validateSyncProduct(product)
            ) {
                is DomainResult.Error -> return validationResult
                is DomainResult.Success -> {}
            }
        }

        allBatches.forEach { batch ->
            when(
                val validationResult = stockBatchValidator.validateSyncStockBatch(batch)
            ) {
                is DomainResult.Error -> return validationResult
                is DomainResult.Success -> {}
            }
        }

        return repository.pushSync(syncPayload, groupId)
    }
}