package com.gorman.network.mappers

import com.gorman.domainmodel.UserData
import com.gorman.network.data.models.UserDataRemote

fun UserDataRemote.toDomain(): UserData =
    UserData(
        uid = uid,
        email = email,
        username = username
    )

fun UserData.toRemote(): UserDataRemote =
    UserDataRemote(
        uid = uid,
        email = email,
        username = username
    )
