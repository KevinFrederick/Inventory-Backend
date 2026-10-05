package model

@JvmInline
value class UserId(val value: String)

data class User(
    val userId: UserId,
    val email: String,
    val passHash: String,
    val name: String,
    val avatarUrl: String?,
    val phoneNumber: String?,
    val jobTitle: String?,
    val locale: String?,
    val timeZone: String?,
    val isActive: Boolean,
    val createdAt: Long,
    val lastUpdated: Long,
)
