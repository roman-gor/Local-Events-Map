package com.gorman.firebase

import com.gorman.domainmodel.MapEvent
import com.gorman.firebase.data.models.MapEventFirebase

fun MapEventFirebase.toDomain(): MapEvent =
    MapEvent(
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
