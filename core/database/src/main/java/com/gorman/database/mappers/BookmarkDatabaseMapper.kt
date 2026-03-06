package com.gorman.database.mappers

import com.gorman.database.data.model.BookmarkDataEntity
import com.gorman.domainmodel.BookmarkData

fun BookmarkData.toEntity(): BookmarkDataEntity = BookmarkDataEntity(
    favoriteEventId = favoriteEventId,
    userId = userId
)
