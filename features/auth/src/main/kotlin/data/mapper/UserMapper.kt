package data.mapper

import data.table.auth.UserTable
import domain.model.User
import domain.model.UserId
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toDomain(): User =
    User(
        userId = UserId(this[UserTable.userId]),
        email = this[UserTable.email],
        passHash = this[UserTable.passHash],
        name = this[UserTable.name],
        avatarUrl = this[UserTable.avatarUrl],
        phoneNumber = this[UserTable.phoneNumber],
        jobTitle = this[UserTable.jobTitle],
        locale = this[UserTable.locale],
        timeZone = this[UserTable.timeZone],
        isActive = this[UserTable.isActive],
        createdAt = this[UserTable.createdAt],
        lastUpdated = this[UserTable.lastUpdated]
    )