package com.gorman.deeplinks.states
sealed interface DeepLinkResult<T> {
    data class Success<T>(val data: T) : DeepLinkResult<T>
    data object NotFound : DeepLinkResult<Nothing>
    data object Network : DeepLinkResult<Nothing>
    data class Error(val message: String) : DeepLinkResult<Nothing>
    data class GenericError<T>(val data: T) : DeepLinkResult<T>
    object Ignored : DeepLinkResult<Nothing>
}
