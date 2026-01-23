package com.gorman.domainmodel

data class UserData(
    val uid: String? = null,
    val email: String? = null,
    val username: String? = null,
    val favouriteEventsIds: HashMap<String, Boolean> = HashMap()
)
