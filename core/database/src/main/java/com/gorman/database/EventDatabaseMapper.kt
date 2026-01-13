package com.gorman.database

import com.gorman.database.data.model.EventEntity
import com.gorman.domain_model.Event

fun EventEntity.toDomain(): Event =
    Event(
        localId = id,
        remoteId = remoteId,
        name = name,
        description = description,
        address = address,
        coordinates = coordinates,
        link = link,
        photo = photo,
        category = category,
        price = price,
        isFavourite = isFavourite
    )

fun Event.toEntity(): EventEntity =
    EventEntity(
        remoteId = remoteId,
        name = name,
        description = description,
        address = address,
        coordinates = coordinates,
        link = link,
        photo = photo,
        category = category,
        price = price,
        isFavourite = isFavourite
    )
