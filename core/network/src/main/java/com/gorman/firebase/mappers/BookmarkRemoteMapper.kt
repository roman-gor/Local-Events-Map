package com.gorman.firebase.mappers

import com.gorman.domainmodel.BookmarkData
import com.gorman.firebase.data.models.BookmarkDataRemote

fun BookmarkDataRemote.toDomain(): BookmarkData = BookmarkData(
    favoriteEventId = favoriteEventId
)

fun BookmarkData.toRemote(): BookmarkDataRemote = BookmarkDataRemote(
    favoriteEventId = favoriteEventId
)
