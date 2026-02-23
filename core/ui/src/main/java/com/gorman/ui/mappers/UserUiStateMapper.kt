package com.gorman.ui.mappers

import com.gorman.domainmodel.UserData
import com.gorman.ui.states.UserUiState

fun UserUiState.toDomain(): UserData =
    UserData(
        uid = uid,
        email = email,
        username = username
    )

fun UserData.toUiState(): UserUiState =
    UserUiState(
        uid = uid,
        email = email,
        username = username
    )
