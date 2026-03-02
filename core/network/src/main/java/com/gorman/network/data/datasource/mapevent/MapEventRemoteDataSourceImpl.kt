package com.gorman.network.data.datasource.mapevent

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.gorman.network.data.models.FirebaseConstants
import com.gorman.network.data.models.MapEventRemote
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

internal class MapEventRemoteDataSourceImpl @Inject constructor(
    databaseReference: DatabaseReference
) : MapEventRemoteDataSource {
    private val database = databaseReference.child(FirebaseConstants.EVENTS_PATH.value)
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

    override fun getAllEvents(): Flow<List<MapEventRemote>> = database.snapshotsFlow().map { snapshot ->
        snapshot.children.mapNotNull { child ->
            child.getValue(MapEventRemote::class.java)
        }
    }

    override suspend fun getAllEventsOnce(): List<MapEventRemote>? = runCatching {
        withTimeout(5000) {
            val snapshot = database.get().await()
            val events = snapshot.children.mapNotNull { snap ->
                snap.getValue(MapEventRemote::class.java)
            }
            Log.d("Events", events.toString())
            events
        }
    }.getOrElse { null }

    override suspend fun getSingleEvent(id: String): Result<MapEventRemote> = runCatching {
        val snapshot = database.child(id).get().await()

        if (!snapshot.exists()) {
            error(NoSuchElementException("Event with id $id not found"))
        }

        snapshot.getValue(MapEventRemote::class.java)
            ?: error(IllegalStateException("Failed to parse event data"))
    }
}
