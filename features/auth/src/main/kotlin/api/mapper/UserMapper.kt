package api.mapper

import api.dto.response.UserResponse
import domain.model.User

fun User.toResponse(): UserResponse =
    UserResponse(
        userId = userId.value,
        name = name,
        email = email,
        avatarUrl = avatarUrl,
        phoneNumber = phoneNumber,
        jobTitle = jobTitle,
        locale = locale,
        timeZone = timeZone,
        isActive = isActive,
        createdAt = createdAt,
        lastUpdated = lastUpdated
    )