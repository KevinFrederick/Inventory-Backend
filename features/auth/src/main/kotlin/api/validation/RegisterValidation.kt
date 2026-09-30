package api.validation

import api.dto.request.RegisterRequest

fun RegisterRequest.validate(): List<String> {
    val errors = mutableListOf<String>()

    if (name.isBlank()) errors.add("Name is required")
    if (email.isBlank()) errors.add("Email is required")
    if (password.isBlank() || confirmPassword.isBlank()) errors.add("Password is required")
    if (password != confirmPassword) errors.add("Passwords do not match")

    return errors
}