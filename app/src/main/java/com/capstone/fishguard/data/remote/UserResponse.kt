package com.capstone.fishguard.data.remote

// Response untuk registrasi
data class RegisterResponse(
    val error: Boolean = false,
    val message: String = "",
    val code: Int? = null,
    val token: String
)

// Response untuk login
data class LoginResponse(
    val error: Boolean = false,
    val message: String = "",
    val code: Int? = null,
    val loginResult: LoginResult = LoginResult()
)

// Hasil login
data class LoginResult(
    val userId: String = "",
    val username: String = "",
    val token: String = ""
)

// Response untuk error
data class ErrorResponse(
    val error: Boolean? = null,
    val message: String? = null
)
