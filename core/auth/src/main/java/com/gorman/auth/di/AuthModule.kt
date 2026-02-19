package com.gorman.auth.di

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
import com.gorman.auth.BuildConfig
import com.gorman.auth.data.authenticator.Authenticator
import com.gorman.auth.data.authenticator.IAuthenticator
import com.gorman.auth.data.googleAuth.GoogleAuthClient
import com.gorman.auth.data.googleAuth.IGoogleAuthClient
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    @Provides
    fun provideCredentialManager(@ApplicationContext context: Context): CredentialManager =
        CredentialManager.create(context)
    @Provides
    fun provideGoogleIdOption(): GetGoogleIdOption =
        GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.WEB_CLIENT_ID)
            .setAutoSelectEnabled(true)
            .build()

    @Provides
    fun provideGetCredentialRequest(googleIdOption: GetGoogleIdOption): GetCredentialRequest =
        GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
}

@Module
@InstallIn(SingletonComponent::class)
internal interface AuthDataModule {
    @Binds
    fun bindAuthenticator(impl: Authenticator): IAuthenticator
    @Binds
    fun bindGoogleAuthClient(impl: GoogleAuthClient): IGoogleAuthClient
}
