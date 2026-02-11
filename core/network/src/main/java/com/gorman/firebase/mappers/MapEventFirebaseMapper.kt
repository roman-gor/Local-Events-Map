package com.gorman.firebase.mappers

import com.gorman.domainmodel.MapEvent
import com.gorman.firebase.data.models.MapEventRemote

fun MapEventRemote.toDomain(): MapEvent =
    MapEvent(
        id = id,
        name = name,
        description = description,
        city = city,
        date = date,
        address = address,
        coordinates = coordinates,
        link = link,
        photo = photo,
        category = category,
        price = price
    )
