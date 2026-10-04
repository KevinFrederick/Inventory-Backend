package domain.usecase.user

data class UserUseCases(
    val getUserById: GetUserByIdUseCase,
    val updateUser: UpdateUserUseCase,
    val deleteUser: DeleteUserUseCase,
)
