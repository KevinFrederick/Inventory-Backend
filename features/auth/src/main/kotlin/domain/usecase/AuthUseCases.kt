package domain.usecase

data class AuthUseCases(
    val register: RegisterUseCase,
    val login: LoginUserUseCase,
    val refreshToken: RefreshTokenUseCase,
    val logout: LogoutUseCase,
)
