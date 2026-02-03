package com.gorman.data.repository.bookmarks

import android.util.Log
import com.gorman.database.data.datasource.dao.BookmarkDao
import com.gorman.database.data.datasource.dao.BookmarkMapEventDao
import com.gorman.database.mappers.toDomain
import com.gorman.database.mappers.toEntity
import com.gorman.domainmodel.BookmarkData
import com.gorman.domainmodel.MapEvent
import com.gorman.firebase.data.datasource.bookmarks.IBookmarksRemoteDataSource
import com.gorman.firebase.mappers.toDomain
import com.gorman.firebase.mappers.toRemote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import kotlin.collections.isNotEmpty
import kotlin.collections.map

class BookmarksRepository @Inject constructor(
    private val bookmarksEventsDataSource: IBookmarksRemoteDataSource,
    private val bookmarksDao: BookmarkDao,
    private val bookmarkMapEventDao: BookmarkMapEventDao
) : IBookmarksRepository {
    override suspend fun updateBookmark(uid: String, bookmark: BookmarkData): Result<Unit> {
        val isBookmarked = bookmarksDao.isBookmarked(bookmark.favoriteEventId)

        if (isBookmarked) {
            bookmarksDao.deleteBookmarkById(bookmark.favoriteEventId)
        } else {
            bookmarksDao.insertBookmark(bookmark.toEntity())
        }

        return bookmarksEventsDataSource.toggleBookmark(uid, bookmark.toRemote())
    }

    override fun getBookmarkedEvents(uid: String): Flow<List<MapEvent>> {
        return bookmarkMapEventDao.loadBookmarksEvents().map { entities ->
            entities.map { it.toDomain() }
        }.onStart {
            syncBookmarks(uid)
        }
    }

    private suspend fun syncBookmarks(uid: String) = runCatching {
        val remoteEvents = bookmarksEventsDataSource.getBookmarksOnce(uid)
        val entities = remoteEvents.map { it.toDomain().toEntity() }
        val remoteIds = entities.map { it.favoriteEventId }
        if (remoteIds.isNotEmpty()) {
            bookmarksDao.updateBookmarks(entities, remoteIds)
        } else {
            bookmarksDao.clearAll()
        }
    }.onFailure { e ->
        Log.d("Sync Bookmarks", "Failed sync with error: ${e.message}")
    }
}
