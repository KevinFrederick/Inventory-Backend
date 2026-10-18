package api.mapper

import api.dto.response.PendingMemberResponse
import domain.model.PendingMember

fun PendingMember.toResponse() = PendingMemberResponse(
    userId = userId.value,
    email = email,
    name = name,
    requestedAt = requestedAt
)