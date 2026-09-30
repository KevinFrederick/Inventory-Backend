package data.table.auth

import org.jetbrains.exposed.v1.core.Table

object UserTable: Table("users") {
    val userId = varchar("user_id", 64)
    val email = varchar("email", 255).uniqueIndex()
    val passHash = varchar("pass_hash", 255)
    val name = varchar("name", 255)

    val avatarUrl = varchar("avatar_url", 512).nullable()
    val phoneNumber = varchar("phone_number", 32).nullable()
    val jobTitle = varchar("job_title", 128).nullable()
    val locale = varchar("locale", 10).nullable()
    val timeZone = varchar("time_zone", 64).nullable()
    val isActive = bool("is_active").default(true)

    val createdAt = long("created_at")
    val lastUpdated = long("last_updated")
    val serverUpdatedAt = long("server_updated_at")

    override val primaryKey: PrimaryKey = PrimaryKey(userId)
}