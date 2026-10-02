package di

import DatabaseFactory
import io.ktor.server.config.ApplicationConfig
import org.koin.dsl.module

fun databaseModule (config: ApplicationConfig) = module {
    single (createdAtStart = true) { DatabaseFactory.init(config) }
}