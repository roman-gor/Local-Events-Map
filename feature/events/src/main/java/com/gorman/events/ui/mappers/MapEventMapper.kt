package com.gorman.events.ui.mappers

import com.gorman.domainmodel.MapEvent
import com.gorman.events.ui.states.MapUiEvent

fun MapEvent.toUiState(): MapUiEvent =
    MapUiEvent(
        id = localId,
        name = name,
        category = category,
        coordinates = coordinates,
        photoUrl = photo,
        isFavourite = isFavourite
    )

fun MapUiEvent.toDomain(): MapEvent =
    MapEvent(
        localId = id,
        name = name,
        category = category,
        coordinates = coordinates,
        photo = photoUrl,
        isFavourite = isFavourite
    )
