package api.mapper

import api.dto.request.StockBatchRequest
import api.dto.response.StockBatchResponse
import domain.model.BatchId
import domain.model.LocationId
import domain.model.ProductId
import domain.model.StockBatch
import domain.model.StockBatchParams

fun StockBatchRequest.toDomainParams(): StockBatchParams =
    StockBatchParams(
        batchId = BatchId(this.batchId),
        productId = ProductId(this.productId),
        locationId = LocationId(this.locationId),
        quantity = this.quantity,
        price = this.price,
        expirationDate = this.expirationDate,
        supplier = this.supplier,
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated
    )

fun StockBatch.toResponse(): StockBatchResponse =
    StockBatchResponse(
        batchId = this.batchId.value,
        productId = this.productId.value,
        location = this.location.toResponse(),
        quantity = this.quantity,
        price = this.price,
        expirationDate = this.expirationDate,
        supplier = this.supplier,
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated
    )