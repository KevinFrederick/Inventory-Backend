package com.kevinfreyap

import com.kevinfreyap.plugins.configureProxySupport
import com.kevinfreyap.plugins.configureRateLimit
import com.kevinfreyap.plugins.configureResources
import com.kevinfreyap.plugins.configureSerialization
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import kotlin.test.*

class ServerTest {

    @Test
    fun `test root endpoint`() = testApplication {
        environment {
            config = MapApplicationConfig()
        }

        application {
            configureSerialization()
            configureResources()
            configureProxySupport()
            configureRateLimit()
            configureRouting()
        }

        // verify server root returns 200
        assertEquals(
            HttpStatusCode.OK,
            client.get("/"){
                header(HttpHeaders.XForwardedFor, "127.0.0.1")
            }
                .status
        )
    }

}
