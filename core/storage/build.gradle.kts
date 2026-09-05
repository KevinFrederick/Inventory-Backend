plugins {
    kotlin("jvm") version "2.4.0"
}

group = "com.kevinfreyap"
version = "unspecified"

dependencies {
    implementation(project(":core:domain"))

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)

    implementation(libs.minio)

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}