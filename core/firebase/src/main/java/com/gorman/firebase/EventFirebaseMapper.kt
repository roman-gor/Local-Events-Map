package com.gorman.firebase

import com.gorman.domain_model.Event
import com.gorman.firebase.data.models.EventFirebase

fun EventFirebase.toDomain(): Event =
    Event(
        remoteId = id,
        name = name,
        description = description,
        address = address,
        coordinates = coordinates,
        link = link,
        photo = photo,
        category = category,
        price = price
    )
