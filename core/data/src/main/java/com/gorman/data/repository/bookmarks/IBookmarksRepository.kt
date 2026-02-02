package com.gorman.data.repository.bookmarks

import com.gorman.domainmodel.BookmarkData
import kotlinx.coroutines.flow.Flow

interface IBookmarksRepository {
    suspend fun updateBookmark(bookmark: BookmarkData): Result<Unit>
    suspend fun getUserId(): String
    suspend fun getBookmarks(): Flow<List<BookmarkData>>
}
