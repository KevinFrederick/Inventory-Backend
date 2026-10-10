package domain.usecase.util

import model.GroupId
import domain.model.StockBatch
import domain.model.StockBatchParams
import domain.repository.LocationRepository
import result.DomainResult
import result.ErrorType

class StockBatchAssembler (
    private val locationRepository: LocationRepository,
) {
    suspend fun assembleBatches(
        batches: List<StockBatchParams>,
        groupId: GroupId
    ): DomainResult<List<StockBatch>> {
        if (batches.isEmpty()) {
            return DomainResult.Success(emptyList())
        }

        val locationIds = batches.map { it.locationId }.distinct()

        val locations = when (
            val locationResult = locationRepository.getLocationsByIds(locationIds, groupId)
        ) {
            is DomainResult.Success -> {
                if (locationResult.data.isEmpty()) {
                    return DomainResult.Error("No locations found", ErrorType.NOT_FOUND)
                }

                locationResult.data
            }
            is DomainResult.Error -> return locationResult
        }

        val locationMap = locations.associateBy { it.locationId }

        val assembledBatches = mutableListOf<StockBatch>()

        for ((batchId, productId, locationId, quantity, price, expirationDate, supplier, createdAt, lastUpdated) in batches) {
            val location = locationMap[locationId]
                ?: return DomainResult.Error("Location not found for batch $batchId", ErrorType.NOT_FOUND)

            assembledBatches.add(
                StockBatch(
                    batchId = batchId,
                    productId = productId,
                    groupId = groupId,
                    location = location,
                    quantity = quantity,
                    price = price,
                    expirationDate = expirationDate,
                    supplier = supplier,
                    createdAt = createdAt,
                    lastUpdated = lastUpdated
                )
            )
        }

        return DomainResult.Success(assembledBatches)
    }
}