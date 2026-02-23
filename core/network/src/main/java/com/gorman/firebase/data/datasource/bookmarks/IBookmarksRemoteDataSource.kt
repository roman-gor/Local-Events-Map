package com.gorman.firebase.data.datasource.bookmarks

import com.gorman.firebase.data.models.BookmarkDataRemote

interface IBookmarksRemoteDataSource {
    suspend fun getBookmarksOnce(uid: String): List<BookmarkDataRemote>
    suspend fun toggleBookmark(uid: String, bookmark: BookmarkDataRemote): Result<Unit>
}
