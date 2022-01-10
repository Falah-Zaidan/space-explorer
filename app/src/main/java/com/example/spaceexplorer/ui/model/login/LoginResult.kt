package com.example.spaceexplorer.ui.model.login

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val success: LoggedInUserView? = null,
    val error: Int? = null,
    val proceed: Boolean = false
)