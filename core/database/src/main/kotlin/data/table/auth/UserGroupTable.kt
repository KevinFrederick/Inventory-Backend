package data.table.auth

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table

object UserGroupTable: Table("user_groups") {
    val userId = reference(
        "user_id",
        UserTable.userId,
        ReferenceOption.CASCADE,
    )
    val groupId = reference(
        "group_id",
        GroupTable.groupId,
        ReferenceOption.CASCADE,
    )
    val role = varchar("role", 64)
    val joinedAt = long("joined_at")
    val serverUpdatedAt = long("server_updated_at")

    override val primaryKey: PrimaryKey = PrimaryKey(userId, groupId)
}