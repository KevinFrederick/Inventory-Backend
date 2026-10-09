package domain.usecase.user

import domain.usecase.user.image.DeleteProfilePictureUseCase
import domain.usecase.user.image.UploadProfilePictureUseCase

data class UserUseCases(
    val getUserById: GetUserByIdUseCase,
    val updateUser: UpdateUserUseCase,
    val uploadProfilePicture: UploadProfilePictureUseCase,
    val deleteUser: DeleteUserUseCase,
    val deleteProfilePicture: DeleteProfilePictureUseCase,
)
