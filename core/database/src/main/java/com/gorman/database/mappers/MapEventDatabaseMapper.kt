package com.gorman.database.mappers

import com.gorman.database.data.model.MapEventEntity
import com.gorman.domainmodel.MapEvent

fun MapEventEntity.toDomain(): MapEvent =
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
        price = price,
        isFavourite = isFavourite
    )

fun MapEvent.toEntity(): MapEventEntity =
    MapEventEntity(
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
        price = price,
        isFavourite = isFavourite
    )
