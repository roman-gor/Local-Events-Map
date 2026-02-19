package com.gorman.auth.data.googleAuth

interface IGoogleAuthClient {
    suspend fun getIdToken(): Result<String>
}
