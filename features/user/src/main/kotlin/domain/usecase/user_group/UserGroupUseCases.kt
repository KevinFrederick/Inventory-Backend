package domain.usecase.user_group

data class UserGroupUseCases(
    val getGroupMembers: GetGroupMembersUseCase,
    val getGroupsForUser: GetGroupsForUserUseCase,
    val addUserToGroup: AddUserToGroupUseCase,
    val updateMemberRole: UpdateMemberRoleUseCase,
    val removeMember: RemoveMemberUseCase,
)
