package com.gorman.ui.mappers

import com.gorman.domainmodel.MapEvent
import com.gorman.ui.states.MapUiEvent

fun MapEvent.toUiState(): MapUiEvent =
    MapUiEvent(
        id = id,
        name = name,
        description = description,
        category = category,
        price = price,
        date = date,
        link = link,
        address = address,
        cityName = city,
        coordinates = coordinates,
        photoUrl = photo,
        isFavourite = isFavourite
    )
