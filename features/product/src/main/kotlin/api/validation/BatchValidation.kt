package api.validation

import api.dto.request.StockBatchRequest

fun StockBatchRequest.validate(): List<String> {
    val errors = mutableListOf<String>()

    if (batchId.isBlank()) errors.add("BatchId cannot be empty")
    if (productId.isBlank()) errors.add("ProductId cannot be empty")
    if (locationId.isBlank()) errors.add("LocationId cannot be empty")
    if (quantity < 0) errors.add("Quantity cannot be lower than 0")
    if (price < 0.0) errors.add("Price cannot be lower than 0")
    if (createdAt < 0L) errors.add("Invalid createdAt timestamp")
    if (lastUpdated < 0L) errors.add("Invalid last updated")

    return errors
}