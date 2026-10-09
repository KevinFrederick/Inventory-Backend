package api.dto.sync

import kotlinx.serialization.Serializable

@Serializable
data class SyncPushResponseDto(
    val groupId: String,
    val success: Boolean,
    val message: String? = null,
    val serverTimeStamp: Long,
)
