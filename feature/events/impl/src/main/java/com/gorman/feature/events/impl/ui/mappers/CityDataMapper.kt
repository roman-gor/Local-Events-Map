package com.gorman.feature.events.impl.ui.mappers

import com.gorman.common.models.CityData
import com.gorman.feature.events.impl.ui.states.CityUiData
import com.gorman.feature.events.impl.ui.states.PointUiState

fun CityData.toUiState(): CityUiData {
    val point = if (latitude != null && longitude != null) {
        latitude?.let { latitude ->
            longitude?.let { longitude ->
                PointUiState(latitude, longitude)
            }
        }
    } else {
        null
    }
    return CityUiData(
        city = city,
        cityName = cityName,
        cityCoordinates = point
    )
}
