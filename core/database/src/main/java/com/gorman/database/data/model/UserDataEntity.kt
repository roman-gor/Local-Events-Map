package com.gorman.database.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserDataEntity(
    @PrimaryKey val uid: String = "",
    val email: String? = null,
    val username: String? = null
)
