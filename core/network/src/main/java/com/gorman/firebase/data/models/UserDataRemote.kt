package com.gorman.firebase.data.models

import kotlinx.serialization.Serializable

@Serializable
data class UserDataRemote(
    val uid: String? = null,
    val email: String? = null,
    val username: String? = null,
    val favouriteEventsIds: HashMap<String, Boolean> = HashMap()
)
