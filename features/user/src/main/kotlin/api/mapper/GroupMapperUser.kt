package api.mapper

import api.dto.response.GroupMemberResponse
import dto.GroupResponse
import dto.UserGroupResponse
import model.Group
import domain.model.GroupMember
import model.GroupWithRole

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
        avatarUrl = this.avatarUrl,
        name = this.name,
        email = this.email,
        role = this.role.name,
        joinedAt = this.joinedAt
    )

fun GroupWithRole.toResponse(): UserGroupResponse =
    UserGroupResponse(
        group = group.toResponse(),
        role = role.name
    )