package com.gorman.firebase.data.datasource

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.gorman.firebase.data.models.EventFirebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseApiImpl @Inject constructor(
    private val database: DatabaseReference
): FirebaseApi {
    override suspend fun getAllEvents(): List<EventFirebase> {
        val eventsRef = database
        return try {
            val eventsSnapshot = eventsRef.get().await()
            eventsSnapshot.children.mapNotNull {
                it.getValue(EventFirebase::class.java)
            }
        } catch(e: Exception) {
            Log.e("Firebase.GetAllEvents", "Ошибка при получении событий ${e.message}")
            emptyList()
        }
    }

    override suspend fun getSingleEvent(id: String): EventFirebase {
        val eventRef = database.child(id)
        return try {
            eventRef.get().await().getValue(EventFirebase::class.java) ?: EventFirebase()
        } catch (e: Exception) {
            Log.e("Firebase.GetAllEvents", "Ошибка при получении события ${e.message}")
            EventFirebase()
        }
    }
}
