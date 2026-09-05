package domain.usecase.util

import domain.model.StockBatch
import domain.model.StockBatchParams
import domain.repository.LocationRepository
import result.DomainResult
import result.ErrorType

class StockBatchAssembler (
    private val locationRepository: LocationRepository,
) {
    suspend fun assembleBatches(
        batches: List<StockBatchParams>
    ): DomainResult<List<StockBatch>> {
        val locationIds = batches.map { it.locationId }.distinct()

        val locationResult = locationRepository.getLocationsByIds(locationIds)
        val locations = (locationResult as? DomainResult.Success)?.data
            ?: return locationResult as DomainResult.Error

        val locationMap = locations.associateBy { it.locationId }

        val assembledBatches = mutableListOf<StockBatch>()

        for (param in batches) {
            val location = locationMap[param.locationId]
                ?: return DomainResult.Error("Location not found for batch ${param.batchId}", ErrorType.NOT_FOUND)

            assembledBatches.add(
                StockBatch(
                    batchId = param.batchId,
                    productId = param.productId,
                    location = location,
                    quantity = param.quantity,
                    price = param.price,
                    expirationDate = param.expirationDate,
                    supplier = param.supplier,
                    createdAt = param.createdAt,
                    lastUpdated = param.lastUpdated
                )
            )
        }

        return DomainResult.Success(assembledBatches)
    }
}