package data.mapper

import data.table.product.CategoryTable
import data.table.user.GroupTable
import domain.model.Category
import domain.model.CategoryId
import model.GroupId
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toCategory(): Category =
    Category(
        categoryId = CategoryId(this[CategoryTable.categoryId]),
        groupId = GroupId(this[GroupTable.groupId]),
        name = this[CategoryTable.name],
        description = this[CategoryTable.description],
        createdAt = this[CategoryTable.createdAt],
        lastUpdated = this[CategoryTable.lastUpdated]
    )