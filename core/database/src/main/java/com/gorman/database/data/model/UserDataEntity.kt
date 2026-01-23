package com.gorman.database.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserDataEntity(
    @PrimaryKey val uid: String = "MbUMS8zxbehpttvhrcMdb6mkO923",
    val email: String? = null,
    val username: String? = null,
    val favouriteEventsIds: List<String> = emptyList()
)
