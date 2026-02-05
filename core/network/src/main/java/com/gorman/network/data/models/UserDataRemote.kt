package com.gorman.network.data.models

import kotlinx.serialization.Serializable

@Serializable
data class UserDataRemote(
    val uid: String = "",
    val email: String? = null,
    val username: String? = null
)
