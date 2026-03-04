package com.gorman.common.models

sealed class DomainException(message: String) : Exception(message) {
    sealed class Auth(message: String) : DomainException(message) {
        class UserNull : Auth("User is null")
        class UserNotFoundOnServer : Auth("User not found on the server")
        class UnexpectedCredential : Auth("Unexpected credential type")
    }

    sealed class Data(message: String) : DomainException(message) {
        class LocationNull : Data("Point Location is null")
    }
}
