package api.mapper

import api.dto.response.GroupMemberResponse
import api.dto.response.GroupResponse
import domain.model.Group
import domain.model.GroupMember

fun Group.toResponse(): GroupResponse =
    GroupResponse(
        groupId = this.groupId.value,
        name = this.name,
        description = this.description,
        address = this.address,
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated
    )

fun GroupMember.toResponse(): GroupMemberResponse =
    GroupMemberResponse(
        userId = this.userId.value,
        name = this.name,
        email = this.email,
        role = this.role.name,
        joinedAt = this.joinedAt
    )