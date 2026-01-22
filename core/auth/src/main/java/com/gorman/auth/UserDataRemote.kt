package com.gorman.auth

data class UserDataRemote(
    val id: String? = null,
    val name: String? = null,
    val favouriteEventsIds: HashMap<String, Boolean> = HashMap()
)
