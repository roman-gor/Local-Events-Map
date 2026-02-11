package com.gorman.firebase.data.datasource.mapevent

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.gorman.firebase.data.models.FirebaseConstants
import com.gorman.firebase.data.models.MapEventRemote
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.io.IOException
import javax.inject.Inject

class MapEventRemoteDataSourceImpl @Inject constructor(
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

    override suspend fun getAllEventsOnce(): List<MapEventRemote>? {
        return try {
            withTimeout(5000) {
                val snapshot = database.get().await()
                val events = snapshot.children.mapNotNull { snap ->
                    snap.getValue(MapEventRemote::class.java)
                }
                Log.d("Events", events.toString())
                events
            }
        } catch (e: TimeoutCancellationException) {
            Log.e("Network Data Source", "Error ${e.message}")
            null
        } catch (e: IOException) {
            Log.e("Network Data Source", "Error ${e.message}")
            null
        }
    }

    override suspend fun getSingleEvent(id: String): Result<MapEventRemote> = runCatching {
        database.child(id).get().await()
    }.mapCatching { snapshot ->
        snapshot.getValue(MapEventRemote::class.java) ?: MapEventRemote()
    }
}
