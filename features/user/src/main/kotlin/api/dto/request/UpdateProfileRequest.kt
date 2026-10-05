package api.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    val name: String? = null,
    val avatarUrl: String? = null,
    val phoneNumber: String? = null,
    val jobTitle: String? = null,
    val locale: String? = null,
    val timeZone: String? = null,
)
