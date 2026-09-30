package data.security

import de.mkammerer.argon2.Argon2Factory
import domain.security.PasswordHasher
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class Argon2PasswordHasher (
    private val pepperSecret: String
): PasswordHasher {
    private val argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)

    override fun hashPassword(password: String): String {
        val pepperedPassword = computeHmac(password, pepperSecret)
        return try {
            argon2.hash(3, 65536, 1, pepperedPassword.toCharArray())
        } finally {
            argon2.wipeArray(pepperedPassword.toCharArray())
        }
    }

    override fun verify(password: String, hash: String): Boolean {
        val pepperedPassword = computeHmac(password, pepperSecret)
        return try {
            argon2.verify(hash, pepperedPassword.toCharArray())
        } finally {
            argon2.wipeArray(pepperedPassword.toCharArray())
        }
    }

    private fun computeHmac(data: String, secret: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(secret.toByteArray(Charsets.UTF_8), "HmacSHA256")

        mac.init(secretKey)
        val hmacBytes = mac.doFinal(data.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(hmacBytes)
    }
}