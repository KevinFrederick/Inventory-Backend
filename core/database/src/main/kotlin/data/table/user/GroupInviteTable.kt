package data.table.user

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table

object GroupInviteTable: Table("group_invite") {
    val code = varchar("invite_code", 32) // The unique shareable string
    val groupId = reference("group_id", GroupTable.groupId, ReferenceOption.CASCADE)
    val createdBy = reference("created_by", UserTable.userId, ReferenceOption.CASCADE)
    val expiresAt = long("expires_at")
    val createdAt = long("created_at")

    override val primaryKey: PrimaryKey = PrimaryKey(code)
}