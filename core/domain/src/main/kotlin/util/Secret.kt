package util

import java.io.File

fun getSecret(secretName: String, envFallback: String): String {
    val secretFile = File("/run/secrets/$secretName")
    if (secretFile.exists()) {
        val content = secretFile.readText().trim()
        if (content.isNotBlank()) return content
    }

    val envValue = System.getenv(envFallback)
    if (!envValue.isNullOrBlank()) {
        return envValue.trim()
    }

    throw IllegalArgumentException("CRITICAL SECURITY ERROR: Secret '$secretName' (or env variable '$envFallback') could not be found!")
}