package com.gorman.data.repository.bookmarks

import android.util.Log
import com.gorman.data.di.BookmarksRepositoryScope
import com.gorman.database.data.datasource.dao.BookmarkDao
import com.gorman.database.data.datasource.dao.BookmarkMapEventDao
import com.gorman.database.mappers.toDomain
import com.gorman.database.mappers.toEntity
import com.gorman.domainmodel.BookmarkData
import com.gorman.domainmodel.MapEvent
import com.gorman.network.data.datasource.bookmarks.IBookmarksRemoteDataSource
import com.gorman.network.mappers.toDomain
import com.gorman.network.mappers.toRemote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.isNotEmpty
import kotlin.collections.map

internal class BookmarksRepository @Inject constructor(
    private val bookmarksEventsDataSource: IBookmarksRemoteDataSource,
    private val bookmarksDao: BookmarkDao,
    private val bookmarkMapEventDao: BookmarkMapEventDao,
    @param:BookmarksRepositoryScope private val externalScope: CoroutineScope
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
        return bookmarkMapEventDao.loadBookmarksEvents()
            .onStart { externalScope.launch { syncBookmarks(uid) } }
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
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
