package com.gorman.auth.data.googleAuth

interface IAuthClient {
    suspend fun getToken(): Result<String>
}
