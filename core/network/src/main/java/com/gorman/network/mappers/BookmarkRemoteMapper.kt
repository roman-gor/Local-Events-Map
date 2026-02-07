package com.gorman.network.mappers

import com.gorman.domainmodel.BookmarkData
import com.gorman.network.data.models.BookmarkDataRemote

fun BookmarkDataRemote.toDomain(): BookmarkData = BookmarkData(
    favoriteEventId = favoriteEventId
)

fun BookmarkData.toRemote(): BookmarkDataRemote = BookmarkDataRemote(
    favoriteEventId = favoriteEventId
)
