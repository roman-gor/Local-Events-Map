package com.gorman.network.mappers

import com.gorman.domainmodel.BookmarkData
import com.gorman.network.data.models.BookmarkDataRemote

fun BookmarkDataRemote.toDomain(uid: String): BookmarkData = BookmarkData(
    favoriteEventId = favoriteEventId,
    userId = uid
)

fun BookmarkData.toRemote(): BookmarkDataRemote = BookmarkDataRemote(
    favoriteEventId = favoriteEventId
)
