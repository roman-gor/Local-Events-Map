package com.gorman.ui.mappers

import com.gorman.domainmodel.MapEvent
import com.gorman.ui.states.MapUiEvent
import com.gorman.ui.utils.DateFormatStyle
import com.gorman.ui.utils.format

fun MapEvent.toUiState(formatType: DateFormatStyle? = null): MapUiEvent =
    MapUiEvent(
        id = id,
        name = name,
        description = description,
        category = category,
        price = price,
        date = date,
        dateDisplay = if (formatType != null) date?.format(formatType) else null,
        link = link,
        address = address,
        cityName = city,
        coordinates = coordinates,
        photoUrl = photo,
        isFavourite = isFavourite
    )
