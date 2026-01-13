package com.gorman.firebase.data.datasource

import com.gorman.firebase.data.models.EventFirebase

interface FirebaseApi {
    suspend fun getAllEvents(): List<EventFirebase>
    suspend fun getSingleEvent(id: String): EventFirebase
}
