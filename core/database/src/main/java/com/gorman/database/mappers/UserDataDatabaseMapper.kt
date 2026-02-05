package com.gorman.database.mappers

import com.gorman.database.data.model.UserDataEntity
import com.gorman.domainmodel.UserData

fun UserDataEntity.toDomain(): UserData =
    UserData(
        uid = uid,
        email = email,
        username = username
    )

fun UserData.toEntity(): UserDataEntity =
    UserDataEntity(
        uid = uid,
        email = email,
        username = username
    )
