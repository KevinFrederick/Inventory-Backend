package api.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import domain.security.TokenProvider
import java.security.SecureRandom
import java.util.Base64
import java.util.Date

class JwtConfig (
    private val secret: String,
    private val issuer: String,
    private val audience: String,
    val accessTokenValidityInMs: Long = 15 * 60 * 1000L,
    override val refreshTokenValidityMs: Long = 30 * 24 * 60 * 60 * 6000L,
): TokenProvider {
    override fun generateAccessToken(userId: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", userId)
            .withExpiresAt(Date(System.currentTimeMillis() + accessTokenValidityInMs))
            .sign(Algorithm.HMAC256(secret))
    }

    override fun generateRefreshToken(): String {
        val randomBytes = ByteArray(32)
        SecureRandom().nextBytes(randomBytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)
    }

    fun makeVerifier(): JWTVerifier {
        return JWT.require(Algorithm.HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()
    }
}