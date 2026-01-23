package com.gorman.featureauth.mappers

import com.gorman.domainmodel.UserData
import com.gorman.featureauth.states.UserUiState
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
