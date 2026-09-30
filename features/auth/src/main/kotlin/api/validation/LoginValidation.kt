package api.validation

import api.dto.request.LoginRequest

fun LoginRequest.validate(): List<String> {
    val errors = mutableListOf<String>()

    if (email.isBlank()) errors.add("Email is required")
    if (password.isBlank()) errors.add("Password is required")

    return errors
}