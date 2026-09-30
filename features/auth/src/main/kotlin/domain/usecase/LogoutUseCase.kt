package domain.usecase

import domain.repository.AuthRepository

class LogoutUseCase (
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(refreshToken: String) {
        authRepository.revokeRefreshToken(refreshToken)
    }
}