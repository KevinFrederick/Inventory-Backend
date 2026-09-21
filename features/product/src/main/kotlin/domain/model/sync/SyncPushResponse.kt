package domain.model.sync

data class SyncPushResponse(
    val success: Boolean,
    val message: String?,
    val serverTimeStamp: Long,
)
