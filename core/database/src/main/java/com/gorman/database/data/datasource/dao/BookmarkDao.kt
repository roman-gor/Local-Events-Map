package com.gorman.database.data.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.gorman.database.data.model.BookmarkDataEntity

@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBookmark(bookmark: BookmarkDataEntity)

    @Transaction
    suspend fun updateBookmarks(bookmarks: List<BookmarkDataEntity>, remoteIds: List<String>) {
        upsertBookmark(bookmarks)
        deleteBookmarksNotIn(remoteIds)
    }

    @Upsert
    suspend fun upsertBookmark(bookmarks: List<BookmarkDataEntity>)

    @Query("DELETE FROM bookmarks WHERE favoriteEventId NOT IN (:remoteIds)")
    suspend fun deleteBookmarksNotIn(remoteIds: List<String>)

    @Query("DELETE FROM bookmarks WHERE favoriteEventId = :id")
    suspend fun deleteBookmarkById(id: String)

    @Query("SELECT EXISTS(SELECT * FROM bookmarks WHERE favoriteEventId = :id)")
    suspend fun isBookmarked(id: String): Boolean

    @Query("DELETE FROM bookmarks")
    suspend fun clearAll()
}
