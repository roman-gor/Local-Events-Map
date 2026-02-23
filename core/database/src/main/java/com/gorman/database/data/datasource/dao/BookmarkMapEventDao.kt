package com.gorman.database.data.datasource.dao

import androidx.room.Dao
import androidx.room.Query
import com.gorman.database.data.model.MapEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkMapEventDao {
    @Query(
        """
        SELECT events.* FROM events 
        INNER JOIN bookmarks ON 
        events.id = bookmarks.favoriteEventId
    """
    )
    fun loadBookmarksEvents(): Flow<List<MapEventEntity>>
}
