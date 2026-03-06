package com.gorman.network.data.datasource.users

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.gorman.network.data.models.FirebaseConstants
import com.gorman.network.data.models.UserDataRemote
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal class UserRemoteDataSourceImpl @Inject constructor(
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

    override suspend fun saveUserToRemote(user: UserDataRemote): Result<Unit> = runCatching {
        val uuid = user.uid
        database.child(uuid).setValue(user).await()
    }

    override fun getUserFromRemote(uid: String): Flow<UserDataRemote?> =
        database
            .child(uid)
            .snapshotsFlow()
            .map { snapshot ->
                snapshot.getValue(UserDataRemote::class.java)
            }

    override suspend fun saveTokenToUser(uid: String, token: String): Result<Unit> = runCatching {
        database
            .child(uid)
            .child(FirebaseConstants.FCM_PATH.value)
            .setValue(token)
            .await()
    }
}
