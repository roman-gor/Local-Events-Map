package com.gorman.featureauth.states

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class UserUiState(
    val uid: String = "",
    val email: String? = null,
    val username: String? = null,
    val favouriteEventsIds: ImmutableList<String> = persistentListOf()
)
