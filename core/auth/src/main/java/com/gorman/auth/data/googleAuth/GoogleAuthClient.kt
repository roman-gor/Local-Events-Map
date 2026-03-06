package com.gorman.auth.data.googleAuth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.gorman.common.models.UnexpectedCredentialError
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class GoogleAuthClient @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val credentialManager: CredentialManager,
    private val credentialRequest: GetCredentialRequest
) : IAuthClient {
    override suspend fun getToken(): Result<String> = runCatching {
        val result = credentialManager.getCredential(
            request = credentialRequest,
            context = context
        )
        val credential = result.credential
        if (credential !is CustomCredential || credential.type !=
            GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            error(UnexpectedCredentialError("Unexpected credentials"))
        }
        val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
        googleCredential.idToken
    }
}
