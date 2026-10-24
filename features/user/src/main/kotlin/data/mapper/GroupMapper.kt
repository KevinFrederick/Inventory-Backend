package data.mapper

import data.table.user.GroupTable
import model.Group
import model.GroupId
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toDomain(totalMembers: Int, totalProducts: Int): Group =
    Group(
        groupId = GroupId(this[GroupTable.groupId]),
        name = this[GroupTable.name],
        description = this[GroupTable.description],
        address = this[GroupTable.address],
        totalMembers = totalMembers,
        totalProducts = totalProducts,
        createdAt = this[GroupTable.createdAt],
        lastUpdated = this[GroupTable.lastUpdated]
    )