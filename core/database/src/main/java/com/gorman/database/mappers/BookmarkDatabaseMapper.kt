package com.gorman.database.mappers

import com.gorman.database.data.model.BookmarkDataEntity
import com.gorman.domainmodel.BookmarkData

fun BookmarkDataEntity.toDomain(): BookmarkData = BookmarkData(
    favoriteEventId = favoriteEventId
)

fun BookmarkData.toEntity(): BookmarkDataEntity = BookmarkDataEntity(
    favoriteEventId = favoriteEventId
)
