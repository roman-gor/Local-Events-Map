package com.gorman.auth.models

data class UserAuthModel(
    val uid: String,
    val username: String?,
    val email: String?
)
