package com.kevinfreyap

import api.security.JwtConfig
import com.kevinfreyap.plugins.configureProxySupport
import com.kevinfreyap.plugins.configureRateLimit
import com.kevinfreyap.plugins.configureResources
import com.kevinfreyap.plugins.configureSerialization
import com.kevinfreyap.plugins.configureSockets
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import io.mockk.mockk
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import kotlin.test.*

class ServerTest {

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `test root endpoint`() = testApplication {
        environment {
            config = MapApplicationConfig()
        }

        application {
            this@application.install(Koin) {
                modules(
                    module {
                        single<JwtConfig> {mockk(relaxed = true)}
                    }
                )
            }

            configureSerialization()
            configureResources()
            configureProxySupport()
            configureRateLimit()
            configureValidation()
            configureSockets()
            configureSecurity()
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
