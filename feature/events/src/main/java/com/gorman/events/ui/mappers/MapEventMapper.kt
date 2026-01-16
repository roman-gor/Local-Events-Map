package com.gorman.events.ui.mappers

import com.gorman.domainmodel.MapEvent
import com.gorman.events.ui.states.MapUiEvent

fun MapEvent.toUiState(): MapUiEvent =
    MapUiEvent(
        id = localId,
        name = name,
        category = category,
        photoUrl = photo,
        isSelected = isSelected,
        isFavourite = isFavourite
    )

fun MapUiEvent.toDomain(): MapEvent =
    MapEvent(
        localId = id,
        name = name,
        category = category,
        photo = photoUrl,
        isSelected = isSelected,
        isFavourite = isFavourite
    )
