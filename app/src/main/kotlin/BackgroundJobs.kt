package com.kevinfreyap

import domain.usecase.DeleteExpiredTokensUseCase
import io.ktor.server.application.Application
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds

fun Application.configureBackgroundJobs() {
    val deleteExpiredTokens: DeleteExpiredTokensUseCase by inject()

    launch {
        while (isActive) {
            deleteExpiredTokens()
            delay(24.hours.inWholeMilliseconds.milliseconds)
        }
    }
}