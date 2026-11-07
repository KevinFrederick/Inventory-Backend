package domain.usecase

data class AuthUseCases(
    val register: RegisterUseCase,
    val login: LoginUserUseCase,
    val authWithGoogle: AuthenticateWithGoogleUseCase,
    val refreshToken: RefreshTokenUseCase,
    val logout: LogoutUseCase,
)
