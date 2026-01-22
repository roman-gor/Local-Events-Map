package com.gorman.auth

interface IAuthDataSource {
    suspend fun loginUser()
}
