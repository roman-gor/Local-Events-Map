package com.gorman.events.ui.mappers

import com.gorman.domainmodel.MapEvent
import com.gorman.events.ui.states.MapUiEvent

fun MapEvent.toUiState(): MapUiEvent =
    MapUiEvent(
        id = id,
        name = name,
        description = description,
        category = category,
        date = date,
        address = address,
        coordinates = coordinates,
        photoUrl = photo,
        isFavourite = isFavourite
    )
