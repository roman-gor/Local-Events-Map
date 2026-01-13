package com.gorman.firebase.domain.repository

import com.gorman.domain_model.Event


interface FirebaseRepository {
    suspend fun getAllEvents(): List<Event>
    suspend fun getSingleEvent(id: String): Event
}
