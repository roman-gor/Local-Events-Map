package com.gorman.firebase.data.datasource.bookmarks

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.gorman.firebase.data.models.BookmarkDataRemote
import com.gorman.firebase.data.models.FirebaseConstants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BookmarksRemoteDataSource @Inject constructor(
    databaseReference: DatabaseReference
): IBookmarksRemoteDataSource {
    private val database = databaseReference.child(FirebaseConstants.BOOKMARKS_PATH.value)
    private fun DatabaseReference.snapshotsFlow(): Flow<DataSnapshot> = callbackFlow {
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        addValueEventListener(eventListener)
        awaitClose { removeEventListener(eventListener) }
    }

    override suspend fun getBookmarks(uid: String): Flow<List<BookmarkDataRemote>> {
        return database
            .child(uid)
            .snapshotsFlow()
            .map { snapshot ->
                snapshot.children.mapNotNull { child ->
                    child.key?.let { eventId ->
                        BookmarkDataRemote(favoriteEventId = eventId)
                    }
                }
            }
    }

    override suspend fun toggleBookmark(uid: String, bookmark: BookmarkDataRemote): Result<Unit> {
        val favRef = database
            .child("$uid/${bookmark.favoriteEventId}")
        return try {
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
        } catch (e: FirebaseException) {
            Log.e("UserRemoteDS", "Error toggling favourites: ${e.message}")
            Result.failure(e)
        }
    }
}
