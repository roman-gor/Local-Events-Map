package com.gorman.firebase.data.repository

import com.gorman.domain_model.Event
import com.gorman.firebase.data.datasource.FirebaseApi
import com.gorman.firebase.domain.repository.FirebaseRepository
import com.gorman.firebase.toDomain
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val firebaseApi: FirebaseApi
): FirebaseRepository {
    override suspend fun getAllEvents(): List<Event> {
        return firebaseApi.getAllEvents().map { it.toDomain() }
    }

    override suspend fun getSingleEvent(id: String): Event {
        return firebaseApi.getSingleEvent(id).toDomain()
    }
}
