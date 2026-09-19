package com.kevinfreyap

import com.kevinfreyap.plugins.configureDI
import com.kevinfreyap.plugins.configureProxySupport
import com.kevinfreyap.plugins.configureRateLimit
import com.kevinfreyap.plugins.configureResources
import com.kevinfreyap.plugins.configureSerialization
import io.ktor.server.application.Application

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDI()

    DatabaseFactory.init()
    initializeDatabaseSchema()

    configureSerialization()
    configureResources()

    configureProxySupport()
    configureRateLimit()

    configureRouting()
}