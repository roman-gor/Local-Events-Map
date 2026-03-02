package com.gorman.auth.data.googleAuth

interface IGoogleAuthClient {
    suspend fun getToken(): Result<String>
}
