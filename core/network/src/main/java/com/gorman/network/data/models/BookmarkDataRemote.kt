package com.gorman.network.data.models

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class BookmarkDataRemote(
    @SerialName("favoriteEventId")
    val favoriteEventId: String
)
