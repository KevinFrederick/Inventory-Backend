package domain.model

@JvmInline
value class RefreshTokenId(val value: String)

data class RefreshToken(
    val id: RefreshTokenId,
    val userId: UserId,
    val token: String,
    val expiresAt: Long,
    val isRevoked: Boolean
)
