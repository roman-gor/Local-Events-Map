package com.gorman.database.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["isActive"])]
)
data class UserDataEntity(
    @PrimaryKey val uid: String = "",
    val email: String? = null,
    val username: String? = null,
    val isActive: Boolean = false
)
