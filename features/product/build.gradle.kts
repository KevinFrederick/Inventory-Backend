plugins {
    kotlin("jvm") version "2.4.0"
    alias(libs.plugins.kotlin.serialization)
}

group = "com.kevinfreyap"
version = "unspecified"

dependencies {
    implementation(project(":core:database"))

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.ktor)

    implementation(ktorLibs.server.resources)
    implementation(libs.kotlinx.serialization)

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}