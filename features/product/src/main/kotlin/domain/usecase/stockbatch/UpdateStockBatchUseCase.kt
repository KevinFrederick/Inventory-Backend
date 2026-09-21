package domain.usecase.stockbatch

import domain.model.BatchId
import domain.model.StockBatch
import domain.model.StockBatchParams
import domain.repository.LocationRepository
import domain.repository.StockBatchRepository
import domain.validation.StockBatchValidator
import result.DomainResult
import result.ErrorType

class UpdateStockBatchUseCase(
    private val batchRepository: StockBatchRepository,
    private val locationRepository: LocationRepository,
    private val stockBatchValidator: StockBatchValidator
) {
    suspend operator fun invoke(
        batchId: BatchId,
        batchParams: StockBatchParams
    ): DomainResult<StockBatch> {
        return if (batchParams.batchId != batchId) {
            DomainResult.Error("Batch id doesn't match", ErrorType.BAD_REQUEST)
        } else {
            val locationResult = locationRepository.getLocationById(batchParams.locationId)

            val location = (locationResult as? DomainResult.Success)?.data
                ?: return locationResult as DomainResult.Error

            val batch = StockBatch(
                batchId = batchParams.batchId,
                productId = batchParams.productId,
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

            batchRepository.updateBatch(batch)
        }
    }
}