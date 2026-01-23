package com.gorman.featureauth.mappers

import com.gorman.domainmodel.UserData
import com.gorman.featureauth.states.UserUiState

fun UserUiState.toDomain(): UserData =
    UserData(
        uid = uid,
        email = email,
        username = username,
        favouriteEventsIds = favouriteEventsIds
    )
