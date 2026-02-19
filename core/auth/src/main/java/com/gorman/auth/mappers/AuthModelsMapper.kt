package com.gorman.auth.mappers

import com.google.firebase.auth.FirebaseUser
import com.gorman.auth.models.UserAuthModel
import com.gorman.domainmodel.UserData

fun FirebaseUser.toAuthModel(): UserAuthModel = UserAuthModel(
    uid = uid,
    email = email,
    username = displayName
)

fun UserAuthModel.toDomain(): UserData = UserData(
    uid = uid,
    email = email,
    username = username
)
