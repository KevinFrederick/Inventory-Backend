package domain.model.sync

data class SyncPushResponse(
    val success: Boolean,
    val message: String? = null,
    val serverTimeStamp: Long,
)
