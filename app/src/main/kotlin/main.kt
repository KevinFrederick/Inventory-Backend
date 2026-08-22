package com.kevinfreyap

import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()

    configureSerialization()
    configureResources()
    configureRouting()
}

fun Application.configureRouting() {
    routing {
        get ("/") {
            call.respondText("Server running!")
        }
    }
}