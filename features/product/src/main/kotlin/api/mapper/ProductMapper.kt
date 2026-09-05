package api.mapper

import api.dto.request.ProductRequest
import api.dto.response.ProductResponse
import domain.model.CategoryId
import domain.model.Product
import domain.model.ProductId
import domain.model.ProductParams

fun ProductRequest.toDomainParams(): ProductParams =
    ProductParams(
        productId = ProductId(this.productId),
        categoryId = CategoryId(this.categoryId),
        name = this.name,
        description = this.description,
        barcode = this.barcode,
        sku = this.sku,
        imageUri = this.imageUri,
        minimumQuantity = this.minimumQuantity,
        batches = this.batches.map { it.toDomainParams() },
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated
    )

fun Product.toResponse(): ProductResponse =
    ProductResponse(
        productId = this.productId.value,
        category = this.category.toResponse(),
        name = this.name,
        description = this.description,
        barcode = this.barcode,
        sku = this.sku,
        imageUri = this.imageUri,
        minimumQuantity = this.minimumQuantity,
        batches = this.batches.map { it.toResponse() },
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated
    )