package com.gorman.firebase.data.datasource.users

import com.google.firebase.FirebaseException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.gorman.firebase.data.models.FirebaseConstants
import com.gorman.firebase.data.models.UserDataRemote
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRemoteDataSourceImpl @Inject constructor(
    databaseReference: DatabaseReference
) : IUserRemoteDataSource {
    private val database = databaseReference.child(FirebaseConstants.USERS_PATH.value)
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

    override suspend fun saveUserToRemote(user: UserDataRemote): Result<Unit> {
        return try {
            val uuid = user.uid
            database.child(uuid).setValue(user).await()
            Result.success(Unit)
        } catch (e: FirebaseException) {
            Result.failure(e)
        }
    }

    override fun getUserFromRemote(uid: String): Flow<UserDataRemote?> {
        return database
            .child(uid)
            .snapshotsFlow()
            .map { snapshot ->
                snapshot.getValue(UserDataRemote::class.java)
            }
    }
}
