package api.mapper

import api.dto.request.CategoryRequest
import api.dto.response.CategoryResponse
import domain.model.Category
import domain.model.CategoryId
import model.GroupId
import util.cleanInlineSpaces
import util.toTitleCase

fun CategoryRequest.toDomain(groupId: GroupId): Category =
    Category(
        categoryId = CategoryId(this.categoryId),
        groupId = groupId,
        name = this.name.cleanInlineSpaces().toTitleCase(),
        description = this.description?.cleanInlineSpaces(),
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated
    )

fun Category.toResponse(): CategoryResponse =
    CategoryResponse(
        categoryId = this.categoryId.value,
        groupId = this.groupId.value,
        name = this.name,
        description = this.description,
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated
    )