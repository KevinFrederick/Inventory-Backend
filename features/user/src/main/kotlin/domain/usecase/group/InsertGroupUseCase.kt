package domain.usecase.group

import domain.model.AppRole
import domain.model.Group
import domain.model.GroupId
import domain.model.UserGroup
import domain.repository.GroupRepository
import domain.validation.GroupValidator
import model.UserId
import result.DomainResult
import java.util.UUID

class InsertGroupUseCase (
    private val groupRepository: GroupRepository,
    private val groupValidator: GroupValidator
) {
    suspend operator fun invoke(
        creatorId: UserId,
        name: String,
        description: String?,
        address: String?,
    ): DomainResult<Group> {
        val timestamp = System.currentTimeMillis()

        val group = Group(
            groupId = GroupId("Group-${UUID.randomUUID()}"),
            name = name,
            description = description,
            address = address,
            createdAt = timestamp,
            lastUpdated = timestamp
        )

        when(
            val validationResult = groupValidator.validateGroup(group)
        ) {
            is DomainResult.Error -> return validationResult
            is DomainResult.Success -> Unit
        }

        val relation = UserGroup(
            userId = creatorId,
            groupId = group.groupId,
            role = AppRole.OWNER,
            joinedAt = timestamp
        )

        return groupRepository.createGroup(
            group = group,
            owner = relation
        )
    }
}