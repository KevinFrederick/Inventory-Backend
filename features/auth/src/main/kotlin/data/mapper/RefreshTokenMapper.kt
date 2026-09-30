package data.mapper

import data.table.auth.RefreshTokenTable
import domain.model.RefreshToken
import domain.model.RefreshTokenId
import domain.model.UserId
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toRefreshToken(): RefreshToken =
    RefreshToken(
        id = RefreshTokenId(this[RefreshTokenTable.id]),
        userId = UserId(this[RefreshTokenTable.userId]),
        token = this[RefreshTokenTable.token],
        expiresAt = this[RefreshTokenTable.expiresAt],
        isRevoked = this[RefreshTokenTable.isRevoked]
    )