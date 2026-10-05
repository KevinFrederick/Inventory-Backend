package domain.usecase.group

data class GroupUseCases(
    val insertGroup: InsertGroupUseCase,
    val getGroupById: GetGroupByIdUseCase,
    val updateGroup: UpdateGroupUseCase,
    val deleteGroup: DeleteGroupUseCase,
)
