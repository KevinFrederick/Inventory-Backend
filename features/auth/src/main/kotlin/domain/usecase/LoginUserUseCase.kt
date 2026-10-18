package domain.usecase

import domain.model.AuthTokens
import domain.model.UserSession
import domain.repository.AuthRepository
import domain.security.PasswordHasher
import domain.security.TokenProvider
import domain.util.hashWithSHA256
import repository.SharedGroupProvider
import result.DomainResult
import result.ErrorType
import validation.ValidationRules

class LoginUserUseCase (
    private val authRepository: AuthRepository,
    private val groupProvider: SharedGroupProvider,
    private val passwordHasher: PasswordHasher,
    private val tokenProvider: TokenProvider,
) {
    suspend operator fun invoke(
        email: String,
        rawPassword: String
    ): DomainResult<UserSession> {
        val sanitizedEmail = email.trim().lowercase()
        val emailErrors = ValidationRules.validateEmail(
            item = sanitizedEmail,
            getId = { "Email" },
            getValue = { it },
        )

        if (emailErrors.isNotEmpty()) {
            return DomainResult.Error(
                emailErrors.joinToString("\n"),
                ErrorType.BAD_REQUEST
            )
        }

        val user = authRepository.findUserByEmail(email)
            ?: return DomainResult.Error(
                "Invalid email or password",
                ErrorType.UNAUTHORIZED
            )

        if (!user.isActive) {
            return DomainResult.Error(
                "Account disabled",
                ErrorType.FORBIDDEN
            )
        }

        val isPasswordCorrect = passwordHasher.verify(rawPassword, user.passHash)
        if (!isPasswordCorrect) {
            return DomainResult.Error(
                "Invalid email or password",
                ErrorType.UNAUTHORIZED
            )
        }

        val userGroups = when(
            val groupResult = groupProvider.getGroupsForUser(userId = user.userId)
        ) {
            is DomainResult.Success -> groupResult.data
            is DomainResult.Error -> return DomainResult.Error(
                "Failed to load user groups",
                ErrorType.UNKNOWN
            )
        }

        val accessToken = tokenProvider.generateAccessToken(user.userId.value)
        val refreshToken = tokenProvider.generateRefreshToken()
        val expiresAt = System.currentTimeMillis() + tokenProvider.refreshTokenValidityMs

        authRepository.saveRefreshToken(
            userId = user.userId,
            token = refreshToken.hashWithSHA256(),
            expiresAt = expiresAt
        )

        return DomainResult.Success(
            UserSession(
                user = user,
                groups = userGroups,
                tokens = AuthTokens(
                    accessToken = accessToken,
                    refreshToken = refreshToken
                )
            )
        )
    }
}