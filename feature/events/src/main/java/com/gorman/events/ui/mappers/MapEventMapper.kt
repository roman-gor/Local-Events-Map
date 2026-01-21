package com.gorman.events.ui.mappers

import com.gorman.domainmodel.MapEvent
import com.gorman.events.ui.states.MapUiEvent

fun MapEvent.toUiState(): MapUiEvent =
    MapUiEvent(
        id = id,
        name = name,
        category = category,
        date = date,
        coordinates = coordinates,
        photoUrl = photo,
        isFavourite = isFavourite
    )
