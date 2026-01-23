package com.gorman.firebase.mappers

import com.gorman.domainmodel.UserData
import com.gorman.firebase.data.models.UserDataRemote

fun UserDataRemote.toDomain(): UserData =
    UserData(
        uid = uid,
        email = email,
        username = username,
        favouriteEventsIds = favouriteEventsIds.map { it.key }
    )

fun UserData.toRemote(): UserDataRemote =
    UserDataRemote(
        uid = uid,
        email = email,
        username = username,
        favouriteEventsIds = HashMap(favouriteEventsIds.associateWith { true })
    )
