package com.gorman.firebase.data.datasource

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.gorman.firebase.data.models.EventFirebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseApiImpl @Inject constructor(
    private val database: DatabaseReference
): FirebaseApi {

    private suspend fun <T> executeRequest(
        operationName: String,
        block: suspend () -> T
    ): T? {
        return try {
            val result = block()
            Log.d("FirebaseAPI", "$operationName успешно выполнено")
            result
        } catch (e: Exception) {
            Log.e("FirebaseAPI", "Ошибка при выполнении $operationName: ${e.message}")
            throw e
        }
    }

    override suspend fun getAllEvents(): List<EventFirebase> = executeRequest("Get All Events") {
        val eventsSnapshot = database.get().await()
        eventsSnapshot.children.mapNotNull {
            it.getValue(EventFirebase::class.java)
        }
    } ?: emptyList()

    override suspend fun getSingleEvent(id: String): EventFirebase =
        executeRequest("Get Single Event") {
            val eventSnapshot = database.child(id).get().await()
            eventSnapshot.getValue(EventFirebase::class.java)
        } ?: EventFirebase()
}
