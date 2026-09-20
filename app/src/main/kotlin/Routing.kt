package com.kevinfreyap

import api.route.categoryRoutes
import api.route.locationRoutes
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import api.route.productRoutes
import api.route.stockBatchRoutes
import api.route.syncRoute

fun Application.configureRouting() {
    routing {
        get ("/") {
            call.respondText("Server running!")
        }

        productRoutes()
        categoryRoutes()
        locationRoutes()
        stockBatchRoutes()
        syncRoute()
    }
}