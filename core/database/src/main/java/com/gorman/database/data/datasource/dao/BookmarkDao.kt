package com.gorman.database.data.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gorman.database.data.model.BookmarkDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBookmark(bookmark: BookmarkDataEntity)

    @Query("DELETE FROM bookmarks WHERE favoriteEventId = :id")
    suspend fun deleteBookmarkById(id: String)

    @Query("SELECT EXISTS(SELECT * FROM bookmarks WHERE favoriteEventId = :id)")
    suspend fun isBookmarked(id: String): Boolean

    @Query("SELECT * FROM bookmarks")
    fun getBookmarks(): Flow<List<BookmarkDataEntity>>
}
