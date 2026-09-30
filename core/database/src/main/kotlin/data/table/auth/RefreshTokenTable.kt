package data.table.auth

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table

object RefreshTokenTable : Table("refresh_tokens") {
    val id = varchar("id", 64)
    val userId = reference(
        "user_id",
        UserTable.userId,
        ReferenceOption.CASCADE
    )
    val token = varchar("token", 128).uniqueIndex()
    val expiresAt = long("expires_at")
    val isRevoked = bool("is_revoked").default(false)
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(id)
}