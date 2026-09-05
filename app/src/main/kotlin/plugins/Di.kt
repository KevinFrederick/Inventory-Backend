package com.kevinfreyap.plugins

import di.databaseModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import di.productModule
import di.storageModule

fun Application.configureDI() {
    install(Koin) {
        slf4jLogger()
        modules(
            databaseModule,
            productModule,
            storageModule
        )
    }
}