package domain.usecase.sync

data class SyncUseCase(
    val syncPull: SyncPullUseCase,
    val syncPush: SyncPushUseCase
)
