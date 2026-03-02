package com.gorman.network.data.datasource.bookmarks

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.database.DatabaseReference
import com.gorman.network.data.models.BookmarkDataRemote
import com.gorman.network.data.models.FirebaseConstants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BookmarksRemoteDataSource @Inject constructor(
    databaseReference: DatabaseReference
) : IBookmarksRemoteDataSource {
    private val database = databaseReference.child(FirebaseConstants.BOOKMARKS_PATH.value)

    override suspend fun getBookmarksOnce(uid: String): List<BookmarkDataRemote> {
        return try {
            val snapshot = database.child(uid).get().await()
            snapshot.children.mapNotNull { child ->
                child.key?.let { eventId ->
                    BookmarkDataRemote(favoriteEventId = eventId)
                }
            }
        } catch (e: FirebaseException) {
            Log.e("RemoteDS", "Error fetching bookmarks", e)
            emptyList()
        }
    }

    override suspend fun toggleBookmark(uid: String, bookmark: BookmarkDataRemote): Result<Unit> {
        val favRef = database
            .child("$uid/${bookmark.favoriteEventId}")
        return runCatching {
            val snapshot = favRef.get().await()
            if (snapshot.exists()) {
                favRef.removeValue().await()
                Log.d("UserRemoteDS", "Event successfully removed from favourites")
                Result.success(Unit)
            } else {
                favRef.setValue(true)
                Log.d("UserRemoteDS", "Event successfully added to favourites")
                Result.success(Unit)
            }
        }
    }
}
