package domain.usecase

import domain.repository.AuthRepository

class DeleteExpiredTokensUseCase (
    private val authRepository: AuthRepository
){
    suspend operator fun invoke() = authRepository.deleteExpiredTokens()
}