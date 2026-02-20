package com.gorman.auth.data.googleAuth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GoogleAuthClient @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val credentialManager: CredentialManager,
    private val credentialRequest: GetCredentialRequest
) : IGoogleAuthClient {
    override suspend fun getIdToken(): Result<String> {
        return try {
            val result = credentialManager.getCredential(
                request = credentialRequest,
                context = context
            )
            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                Result.success(googleCredential.idToken)
            } else {
                Result.failure(Exception("Unexpected credential type"))
            }
        } catch (e: GetCredentialException) {
            Result.failure(e)
        }
    }
}
