package di

import data.repository.GroupRepositoryImpl
import data.repository.UserRepositoryImpl
import domain.repository.GroupRepository
import domain.repository.UserRepository
import domain.usecase.group.DeleteGroupUseCase
import domain.usecase.group.GetGroupByIdUseCase
import domain.usecase.group.GroupUseCases
import domain.usecase.group.InsertGroupUseCase
import domain.usecase.group.UpdateGroupUseCase
import domain.usecase.user.DeleteUserUseCase
import domain.usecase.user.GetUserByIdUseCase
import domain.usecase.user.UpdateUserUseCase
import domain.usecase.user.UserUseCases
import domain.usecase.user_group.AddUserToGroupUseCase
import domain.usecase.user_group.GetGroupMembersUseCase
import domain.usecase.user_group.GetGroupsForUserUseCase
import domain.usecase.user_group.RemoveMemberUseCase
import domain.usecase.user_group.UpdateMemberRoleUseCase
import domain.usecase.user_group.UserGroupUseCases
import domain.validation.GroupValidator
import domain.validation.UserValidator
import org.koin.dsl.module

val userModule = module {
    single <UserRepository> { UserRepositoryImpl() }
    single <GroupRepository> { GroupRepositoryImpl() }

    factory { UserValidator() }
    factory { GroupValidator() }

    factory { GetUserByIdUseCase(get()) }
    factory { UpdateUserUseCase(get(), get()) }
    factory { DeleteUserUseCase(get()) }
    factory {
        UserUseCases(
            getUserById = get(),
            updateUser = get(),
            deleteUser = get()
        )
    }

    factory { GetGroupByIdUseCase(get()) }
    factory { InsertGroupUseCase(get(), get()) }
    factory { UpdateGroupUseCase(get(), get()) }
    factory { DeleteGroupUseCase(get()) }
    factory {
        GroupUseCases(
            insertGroup = get(),
            getGroupById = get(),
            updateGroup = get(),
            deleteGroup = get()
        )
    }

    factory { GetGroupMembersUseCase(get()) }
    factory { GetGroupsForUserUseCase(get()) }
    factory { AddUserToGroupUseCase(get(), get()) }
    factory { UpdateMemberRoleUseCase(get()) }
    factory { RemoveMemberUseCase(get()) }
    factory {
        UserGroupUseCases(
            getGroupMembers = get(),
            getGroupsForUser = get(),
            addUserToGroup = get(),
            updateMemberRole = get(),
            removeMember = get()
        )
    }
}