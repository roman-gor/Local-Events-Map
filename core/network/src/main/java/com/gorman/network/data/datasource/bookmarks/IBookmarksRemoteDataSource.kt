package com.gorman.network.data.datasource.bookmarks

import com.gorman.network.data.models.BookmarkDataRemote

interface IBookmarksRemoteDataSource {
    suspend fun getBookmarksOnce(uid: String): List<BookmarkDataRemote>
    suspend fun toggleBookmark(uid: String, bookmark: BookmarkDataRemote): Result<Unit>
}
