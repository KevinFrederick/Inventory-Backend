package domain.usecase.membership

data class MembershipUseCases(
    val getPendingMembers: GetPendingMembersUseCase,
    val generateInviteCode: GenerateInviteCodeUseCase,
    val joinGroupWithCode: JoinGroupWithCodeUseCase,
    val approvePendingMember: ApprovePendingMemberUseCase,
    val rejectPendingMember: RejectPendingMemberUseCase,
)
