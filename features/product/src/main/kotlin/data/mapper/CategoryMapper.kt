package data.mapper

import data.table.product.CategoryTable
import domain.model.Category
import domain.model.CategoryId
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toCategory(): Category =
    Category(
        categoryId = CategoryId(this[CategoryTable.categoryId]),
        name = this[CategoryTable.name],
        description = this[CategoryTable.description],
        createdAt = this[CategoryTable.createdAt],
        lastUpdated = this[CategoryTable.lastUpdated]
    )