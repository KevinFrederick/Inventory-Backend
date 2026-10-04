package domain.usecase

import domain.model.AuthTokens
import model.User
import model.UserId
import domain.model.UserSession
import domain.repository.AuthRepository
import domain.security.PasswordHasher
import domain.security.TokenProvider
import domain.validation.ValidationRulesAuth
import result.DomainResult
import result.ErrorType
import java.util.UUID

class RegisterUseCase (
    private val authRepository: AuthRepository,
    private val passwordHasher: PasswordHasher,
    private val tokenProvider: TokenProvider,
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        rawPassword: String
    ): DomainResult<UserSession> {
        val sanitizedEmail = email.trim().lowercase()
        val sanitizedName = name.trim()

        val errors = mutableListOf<String>()

        if (sanitizedName.length > 128) {
            errors.add("Name cannot exceed 128 characters")
        }

        errors.addAll(ValidationRulesAuth.validateEmail(sanitizedEmail))
        errors.addAll(ValidationRulesAuth.validatePassword(rawPassword))

        if (errors.isNotEmpty()) {
            return DomainResult.Error(
                errors.joinToString("\n"),
                ErrorType.BAD_REQUEST
            )
        }

        val hashPassword = passwordHasher.hashPassword(rawPassword)

        val timeStamp = System.currentTimeMillis()
        val newUser = User(
            userId = UserId(UUID.randomUUID().toString()),
            email = sanitizedEmail,
            passHash = hashPassword,
            name = sanitizedName,
            avatarUrl = null,
            phoneNumber = null,
            jobTitle = null,
            locale = null,
            timeZone = null,
            isActive = true,
            createdAt = timeStamp,
            lastUpdated = timeStamp
        )

        return when(
            val result = authRepository.registerUser(newUser)
        ) {
            is DomainResult.Success -> {
                val createdUser = result.data
                val accessToken = tokenProvider.generateAccessToken(createdUser.userId.value)
                val refreshToken = tokenProvider.generateRefreshToken()
                val expiresAt = System.currentTimeMillis() + tokenProvider.refreshTokenValidityMs
                
                authRepository.saveRefreshToken(
                    userId = createdUser.userId,
                    token = refreshToken,
                    expiresAt = expiresAt
                )

                DomainResult.Success(
                    UserSession(
                        user = createdUser,
                        tokens = AuthTokens(
                            accessToken = accessToken,
                            refreshToken = refreshToken
                        )
                    )
                )
            }
            is DomainResult.Error -> result
        }
    }
}