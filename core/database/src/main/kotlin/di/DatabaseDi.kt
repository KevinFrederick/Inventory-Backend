package di

import DatabaseFactory
import org.koin.dsl.module

val databaseModule = module {
    single { DatabaseFactory }
}