package com.gorman.firebase.data.datasource.bookmarks

import com.gorman.firebase.data.models.BookmarkDataRemote
import kotlinx.coroutines.flow.Flow

interface IBookmarksRemoteDataSource {
    suspend fun getBookmarks(uid: String): Flow<List<BookmarkDataRemote>>
    suspend fun toggleBookmark(uid: String, bookmark: BookmarkDataRemote): Result<Unit>
}
