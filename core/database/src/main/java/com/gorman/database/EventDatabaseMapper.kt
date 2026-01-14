package com.gorman.database

import com.gorman.database.data.model.MapEventEntity
import com.gorman.domain_model.MapEvent

fun MapEventEntity.toDomain(): MapEvent =
    MapEvent(
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

fun MapEvent.toEntity(): MapEventEntity =
    MapEventEntity(
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
