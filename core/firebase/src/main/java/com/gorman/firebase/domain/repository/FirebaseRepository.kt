package com.gorman.firebase.domain.repository

import com.gorman.common.Event

interface FirebaseRepository {
    suspend fun getAllEvents(): List<Event>
    suspend fun getSingleEvent(id: String): Event
}
