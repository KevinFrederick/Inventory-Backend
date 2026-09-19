package util

import java.io.File

fun getSecret(secretName: String, envFallback: String): String {
    val secretFile = File("/run/secrets/$secretName")
    return if (secretFile.exists()) {
        secretFile.readText().trim()
    } else {
        System.getenv(envFallback) ?: "default"
    }
}