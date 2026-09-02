package com.kevinfreyap

import api.categoryRoute
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import api.productRoutes

fun Application.configureRouting() {
    routing {
        get ("/") {
            call.respondText("Server running!")
        }

        productRoutes()
        categoryRoute()
    }
}