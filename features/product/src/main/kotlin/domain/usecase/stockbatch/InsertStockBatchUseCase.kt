package domain.usecase.stockbatch

import domain.model.StockBatch
import domain.model.StockBatchParams
import domain.repository.LocationRepository
import domain.repository.StockBatchRepository
import result.DomainResult

class InsertStockBatchUseCase (
    private val batchRepository: StockBatchRepository,
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(batchParams: StockBatchParams): DomainResult<StockBatch> {
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

        return batchRepository.insertBatch(batch)
    }
}