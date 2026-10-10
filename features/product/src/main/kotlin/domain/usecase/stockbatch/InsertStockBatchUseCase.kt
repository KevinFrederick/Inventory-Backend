package domain.usecase.stockbatch

import model.GroupId
import domain.model.StockBatch
import domain.model.StockBatchParams
import domain.repository.LocationRepository
import domain.repository.ProductRepository
import domain.repository.StockBatchRepository
import domain.validation.StockBatchValidator
import result.DomainResult

class InsertStockBatchUseCase (
    private val batchRepository: StockBatchRepository,
    private val locationRepository: LocationRepository,
    private val productRepository: ProductRepository,
    private val stockBatchValidator: StockBatchValidator
) {
    suspend operator fun invoke(
        batchParams: StockBatchParams,
        groupId: GroupId,
    ): DomainResult<StockBatch> {
        when(
            val productResult = productRepository.getProductById(batchParams.productId, groupId)
        ) {
            is DomainResult.Error -> return productResult
            is DomainResult.Success -> Unit
        }

        val location = when (
            val locationResult = locationRepository.getLocationById(batchParams.locationId, groupId)
        ) {
            is DomainResult.Success -> locationResult.data
            is DomainResult.Error -> return locationResult
        }

        val batch = StockBatch(
            batchId = batchParams.batchId,
            productId = batchParams.productId,
            groupId = groupId,
            location = location,
            quantity = batchParams.quantity,
            price = batchParams.price,
            expirationDate = batchParams.expirationDate,
            supplier = batchParams.supplier,
            createdAt = batchParams.createdAt,
            lastUpdated = batchParams.lastUpdated
        )

        when(
            val validationResult = stockBatchValidator.validateStockBatch(batch)
        ) {
            is DomainResult.Error -> return validationResult
            is DomainResult.Success -> {}
        }

        return batchRepository.insertBatch(batch, groupId)
    }
}