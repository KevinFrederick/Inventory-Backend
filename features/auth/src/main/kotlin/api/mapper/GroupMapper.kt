package api.mapper

import dto.GroupResponse
import dto.UserGroupResponse
import model.Group
import model.GroupWithRole

fun Group.toResponse(): GroupResponse =
    GroupResponse(
        groupId = groupId.value,
        name = name,
        description = description,
        address = address,
        createdAt = createdAt,
        lastUpdated = lastUpdated
    )

fun GroupWithRole.toResponse(): UserGroupResponse =
    UserGroupResponse(
        group = group.toResponse(),
        role = role.name
    )