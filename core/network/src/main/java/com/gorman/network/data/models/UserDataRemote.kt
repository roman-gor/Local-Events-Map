package com.gorman.network.data.models

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class UserDataRemote(
    @SerialName("uid")
    val uid: String = "",
    @SerialName("email")
    val email: String? = null,
    @SerialName("username")
    val username: String? = null
)
