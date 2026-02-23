package com.gorman.database.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("bookmarks")
data class BookmarkDataEntity(
    @PrimaryKey val favoriteEventId: String = ""
)
