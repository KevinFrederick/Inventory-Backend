plugins {
    kotlin("jvm") version "2.4.0"
}

group = "com.kevinfreyap"
version = "unspecified"

dependencies {
    api(ktorLibs.server.rateLimit)
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}