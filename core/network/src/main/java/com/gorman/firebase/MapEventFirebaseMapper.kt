package com.gorman.firebase

import com.gorman.domainmodel.MapEvent
import com.gorman.firebase.data.models.MapEventFirebase

fun MapEventFirebase.toDomain(): MapEvent =
    MapEvent(
        id = id,
        name = name,
        description = description,
        city = city,
        address = address,
        coordinates = coordinates,
        link = link,
        photo = photo,
        category = category,
        price = price
    )
