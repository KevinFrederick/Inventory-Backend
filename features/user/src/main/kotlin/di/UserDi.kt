package di

import data.repository.GroupMembershipRepositoryImpl
import data.repository.GroupRepositoryImpl
import data.repository.InviteRepositoryImpl
import data.repository.UserRepositoryImpl
import domain.repository.GroupMembershipRepository
import domain.repository.GroupRepository
import domain.repository.InviteRepository
import domain.repository.UserRepository
import domain.usecase.group.DeleteGroupUseCase
import domain.usecase.group.GetGroupByIdUseCase
import domain.usecase.group.GroupUseCases
import domain.usecase.group.InsertGroupUseCase
import domain.usecase.group.UpdateGroupUseCase
import domain.usecase.membership.ApprovePendingMemberUseCase
import domain.usecase.membership.GenerateInviteCodeUseCase
import domain.usecase.membership.GetPendingMembersUseCase
import domain.usecase.membership.JoinGroupWithCodeUseCase
import domain.usecase.membership.MembershipUseCases
import domain.usecase.membership.RejectPendingMemberUseCase
import domain.usecase.user.DeleteUserUseCase
import domain.usecase.user.GetUserByIdUseCase
import domain.usecase.user.UpdateUserUseCase
import domain.usecase.user.UserUseCases
import domain.usecase.user.image.DeleteProfilePictureUseCase
import domain.usecase.user.image.UploadProfilePictureUseCase
import domain.usecase.user_group.AddUserToGroupUseCase
import domain.usecase.user_group.GetGroupMembersUseCase
import domain.usecase.user_group.GetGroupsForUserUseCase
import domain.usecase.user_group.RemoveMemberUseCase
import domain.usecase.user_group.UpdateMemberRoleUseCase
import domain.usecase.user_group.UserGroupUseCases
import domain.validation.GroupValidator
import domain.validation.UserValidator
import org.koin.core.qualifier.named
import org.koin.dsl.binds
import org.koin.dsl.module
import repository.SharedGroupProvider
import repository.UserRoleProvider
import usecase.VerifyUserGroupRoleUseCase

val userModule = module {
    single <UserRepository> { UserRepositoryImpl() }
    single { GroupRepositoryImpl() } binds arrayOf(GroupRepository::class, UserRoleProvider::class, SharedGroupProvider::class)
    single <InviteRepository> { InviteRepositoryImpl() }
    single <GroupMembershipRepository> { GroupMembershipRepositoryImpl() }

    factory { UserValidator() }
    factory { GroupValidator() }

    factory { GetUserByIdUseCase(get()) }
    factory { UpdateUserUseCase(get(), get()) }
    factory { UploadProfilePictureUseCase(get(named("UserStorage")), get()) }
    factory { DeleteUserUseCase(get()) }
    factory { DeleteProfilePictureUseCase(get(named("UserStorage")), get()) }
    factory {
        UserUseCases(
            getUserById = get(),
            updateUser = get(),
            deleteUser = get(),
            uploadProfilePicture = get(),
            deleteProfilePicture = get()
        )
    }

    factory { GetGroupByIdUseCase(get()) }
    factory { InsertGroupUseCase(get(), get()) }
    factory { UpdateGroupUseCase(get(), get(), get()) }
    factory { DeleteGroupUseCase(get(), get()) }
    factory {
        GroupUseCases(
            insertGroup = get(),
            getGroupById = get(),
            updateGroup = get(),
            deleteGroup = get()
        )
    }

    factory { GetGroupMembersUseCase(get(), get()) }
    factory { GetGroupsForUserUseCase(get()) }
    factory { AddUserToGroupUseCase(get(), get(), get()) }
    factory { UpdateMemberRoleUseCase(get(), get()) }
    factory { RemoveMemberUseCase(get(), get()) }
    factory {
        UserGroupUseCases(
            getGroupMembers = get(),
            getGroupsForUser = get(),
            addUserToGroup = get(),
            updateMemberRole = get(),
            removeMember = get()
        )
    }

    factory { GetPendingMembersUseCase(get(), get()) }
    factory { GenerateInviteCodeUseCase(get(), get()) }
    factory { JoinGroupWithCodeUseCase(get(), get()) }
    factory { ApprovePendingMemberUseCase(get(), get()) }
    factory { RejectPendingMemberUseCase(get(), get()) }
    factory {
        MembershipUseCases(
            getPendingMembers = get(),
            generateInviteCode = get(),
            joinGroupWithCode = get(),
            approvePendingMember = get(),
            rejectPendingMember = get()
        )
    }

    factory { VerifyUserGroupRoleUseCase(get()) }
}