plugins {
    kotlin("jvm") version "2.4.0"
    alias(libs.plugins.kotlin.serialization)
}

group = "com.kevinfreyap"
version = "unspecified"

dependencies {
    implementation(project(":core:domain"))

    implementation(ktorLibs.server.resources)
    implementation(ktorLibs.server.auth.jwt)

    implementation(libs.kotlinx.serialization)

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}