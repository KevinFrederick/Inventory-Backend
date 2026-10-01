package domain.usecase

import domain.model.AuthTokens
import domain.repository.AuthRepository
import domain.security.TokenProvider
import domain.util.hashWithSHA256
import result.DomainResult
import result.ErrorType

class RefreshTokenUseCase (
    private val authRepository: AuthRepository,
    private val tokenProvider: TokenProvider
) {
    suspend operator fun invoke(providedRefreshToken: String): DomainResult<AuthTokens> {
        val hashedToken = providedRefreshToken.hashWithSHA256()

        val savedToken = authRepository.findRefreshToken(hashedToken)
            ?: return DomainResult.Error("Invalid or expired refresh token", ErrorType.UNAUTHORIZED)

        if (savedToken.isRevoked) {
            authRepository.revokeAllUserTokens(savedToken.userId)
            return DomainResult.Error("Security Violation: Token Revoked", ErrorType.UNAUTHORIZED)
        }

        if (System.currentTimeMillis() > savedToken.expiresAt) {
            authRepository.revokeRefreshToken(hashedToken)
            return DomainResult.Error("Refresh token expired", ErrorType.UNAUTHORIZED)
        }

        authRepository.revokeRefreshToken(hashedToken)

        val newAccessToken = tokenProvider.generateAccessToken(savedToken.userId.value)
        val newRefreshToken = tokenProvider.generateRefreshToken()
        val newExpiresAt = System.currentTimeMillis() + tokenProvider.refreshTokenValidityMs

        authRepository.saveRefreshToken(
            userId = savedToken.userId,
            token = newRefreshToken.hashWithSHA256(),
            expiresAt = newExpiresAt
        )

        return DomainResult.Success(
            AuthTokens(
                accessToken = newAccessToken,
                refreshToken = newRefreshToken
            )
        )
    }
}