package com.gorman.database.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "bookmarks",
    primaryKeys = ["favoriteEventId", "userId"],
    foreignKeys = [
        ForeignKey(
            entity = UserDataEntity::class,
            parentColumns = ["uid"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class BookmarkDataEntity(
    val favoriteEventId: String = "",
    val userId: String = ""
)
