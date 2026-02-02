package com.gorman.data.repository.bookmarks

import com.gorman.database.data.datasource.dao.BookmarkDao
import com.gorman.database.data.datasource.dao.UserDataDao
import com.gorman.database.mappers.toDomain
import com.gorman.database.mappers.toEntity
import com.gorman.domainmodel.BookmarkData
import com.gorman.firebase.data.datasource.bookmarks.IBookmarksRemoteDataSource
import com.gorman.firebase.mappers.toRemote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookmarksRepository @Inject constructor(
    private val bookmarksEventsDataSource: IBookmarksRemoteDataSource,
    private val bookmarksDao: BookmarkDao,
    private val userDataDao: UserDataDao
): IBookmarksRepository {
    override suspend fun updateBookmark(bookmark: BookmarkData): Result<Unit> {
        val uid = try {
            getUserId()
        } catch (e: Exception) {
            return Result.failure(e)
        }

        val isBookmarked = bookmarksDao.isBookmarked(bookmark.favoriteEventId)

        if (isBookmarked) {
            bookmarksDao.deleteBookmarkById(bookmark.favoriteEventId)
        } else {
            bookmarksDao.insertBookmark(bookmark.toEntity())
        }

        return bookmarksEventsDataSource.toggleBookmark(uid, bookmark.toRemote())
    }

    override suspend fun getUserId(): String {
        return userDataDao.getUser().map { it.uid }.first()
    }

    override suspend fun getBookmarks(): Flow<List<BookmarkData>> {
        return bookmarksDao.getBookmarks().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
