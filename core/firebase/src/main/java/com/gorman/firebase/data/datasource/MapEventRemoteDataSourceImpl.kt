package com.gorman.firebase.data.datasource

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.gorman.firebase.data.models.MapEventFirebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MapEventRemoteDataSourceImpl @Inject constructor(
    private val database: DatabaseReference
) : MapEventRemoteDataSource {
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

    override fun getAllEvents(): Flow<List<MapEventFirebase>> = database.snapshotsFlow().map { snapshot ->
        snapshot.children.mapNotNull { child ->
            child.getValue(MapEventFirebase::class.java)
        }
    }

    override suspend fun getSingleEvent(id: String): Result<MapEventFirebase> = runCatching {
        database.child(id).get().await()
    }.mapCatching { snapshot ->
        snapshot.getValue(MapEventFirebase::class.java) ?: MapEventFirebase()
    }
}
