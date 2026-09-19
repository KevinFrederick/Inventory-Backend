package com.kevinfreyap.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.forwardedheaders.XForwardedHeaders

fun Application.configureProxySupport() {
    install(XForwardedHeaders)
}