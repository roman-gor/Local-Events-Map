package com.gorman.data.repository.bookmarks

import com.gorman.domainmodel.BookmarkData
import com.gorman.domainmodel.MapEvent
import kotlinx.coroutines.flow.Flow

interface IBookmarksRepository {
    suspend fun updateBookmark(uid: String, bookmark: BookmarkData): Result<Unit>
    fun getBookmarkedEvents(uid: String): Flow<List<MapEvent>>
}
