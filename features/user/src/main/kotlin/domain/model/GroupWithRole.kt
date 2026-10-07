package domain.model

import model.AppRole

data class GroupWithRole(
    val group: Group,
    val role: AppRole
)
