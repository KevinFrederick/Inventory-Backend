package api.mapper

import api.dto.request.CategoryRequest
import api.dto.response.CategoryResponse
import domain.model.Category
import domain.model.CategoryId

fun CategoryRequest.toDomain(): Category =
    Category(
        categoryId = CategoryId(this.categoryId),
        name = this.name,
        description = this.description,
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated
    )

fun Category.toResponse(): CategoryResponse =
    CategoryResponse(
        categoryId = this.categoryId.value,
        name = this.name,
        description = this.description,
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated
    )