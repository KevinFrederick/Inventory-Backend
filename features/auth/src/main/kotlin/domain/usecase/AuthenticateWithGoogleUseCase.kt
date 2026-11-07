package domain.usecase

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import domain.model.AuthTokens
import domain.model.UserSession
import domain.repository.AuthRepository
import domain.security.PasswordHasher
import domain.security.TokenProvider
import model.Group
import model.GroupId
import model.User
import model.UserId
import repository.SharedGroupProvider
import result.DomainResult
import result.ErrorType
import java.util.Collections
import java.util.UUID

class AuthenticateWithGoogleUseCase (
    private val authRepository: AuthRepository,
    private val groupProvider: SharedGroupProvider,
    private val tokenProvider: TokenProvider,
    private val passwordHasher: PasswordHasher,
    googleClientId: String
) {
    private val verifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory())
        .setAudience(Collections.singletonList(googleClientId))
        .build()

    suspend operator fun invoke(
        idToken: String,
    ): DomainResult<UserSession> {
        val googleIdToken = try {
            verifier.verify(idToken)
        } catch (e: Exception) {
            return DomainResult.Error("Invalid Google token: ${e.localizedMessage}", ErrorType.UNAUTHORIZED)
        } ?: return DomainResult.Error("Invalid Google token", ErrorType.UNAUTHORIZED)

        val payload = googleIdToken.payload
        val email = payload.email

        if (email.isNullOrBlank()) {
            return DomainResult.Error("Google account does not provide a valid email", ErrorType.UNAUTHORIZED)
        }

        val name = payload["name"] as String? ?: "Google User"

        var user = try {
            authRepository.findUserByEmail(email)
        } catch (e: Exception) {
            return DomainResult.Error("Database error during user lookup: ${e.localizedMessage}", ErrorType.UNKNOWN)
        }

        if (user == null) {
            val randomPassword = UUID.randomUUID().toString()
            val hashedPassword = passwordHasher.hashPassword(randomPassword)

            val timeStamp = System.currentTimeMillis()
            val newUser = User(
                userId = UserId(UUID.randomUUID().toString()),
                email = email,
                passHash = hashedPassword,
                name = name,
                avatarUrl = null,
                phoneNumber = null,
                jobTitle = null,
                locale = null,
                timeZone = null,
                isActive = true,
                createdAt = timeStamp,
                lastUpdated = timeStamp
            )

            val newGroup = Group(
                groupId = GroupId("Group-${UUID.randomUUID()}"),
                name = "${name}'s Inventory",
                description = null,
                address = null,
                totalMembers = 1,
                totalProducts = 0,
                createdAt = timeStamp,
                lastUpdated = timeStamp
            )

            when(
                val result = authRepository.registerUser(newUser, newGroup)
            ) {
                is DomainResult.Success -> {
                    user = result.data
                }
                is DomainResult.Error -> {
                    return result
                }
            }
        }

        if (!user.isActive) {
            return DomainResult.Error(
                "Account disabled",
                ErrorType.FORBIDDEN
            )
        }

        val accessToken = tokenProvider.generateAccessToken(user.userId.value)
        val refreshToken = tokenProvider.generateRefreshToken()
        val expiresAt = System.currentTimeMillis() + tokenProvider.refreshTokenValidityMs

        authRepository.saveRefreshToken(
            userId = user.userId,
            token = refreshToken,
            expiresAt = expiresAt
        )

        val userGroups = when(
            val groupResult = groupProvider.getGroupsForUser(userId = user.userId)
        ) {
            is DomainResult.Success -> groupResult.data
            is DomainResult.Error -> return DomainResult.Error(
                "Failed to load user groups",
                ErrorType.UNKNOWN
            )
        }

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