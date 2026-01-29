package com.gorman.feature.auth.impl.mappers

import com.gorman.domainmodel.UserData
import com.gorman.feature.auth.impl.states.UserUiState
import kotlinx.collections.immutable.toImmutableList

fun UserUiState.toDomain(): UserData =
    UserData(
        uid = uid,
        email = email,
        username = username,
        favouriteEventsIds = favouriteEventsIds
    )

fun UserData.toUiState(): UserUiState =
    UserUiState(
        uid = uid,
        email = email,
        username = username,
        favouriteEventsIds = favouriteEventsIds.toImmutableList()
    )
