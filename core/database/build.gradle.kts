plugins {
    kotlin("jvm") version "2.4.0"
}

group = "com.kevinfreyap"
version = "unspecified"

dependencies {
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)

    api(libs.exposed.core)
    api(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.javatime)

    implementation(libs.postgresql)
    implementation(libs.hikaricp)

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}