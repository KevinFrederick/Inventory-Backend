package domain.model.sync

import model.GroupId

data class SyncPushResponse(
    val groupId: GroupId,
    val success: Boolean,
    val message: String?,
    val serverTimeStamp: Long,
)
