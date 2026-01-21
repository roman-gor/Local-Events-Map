package com.gorman.events.ui.mappers

import com.gorman.common.models.CityData
import com.gorman.events.ui.states.CityUiData
import com.yandex.mapkit.geometry.Point

fun CityData.toUiState(): CityUiData {
    val point = if (latitude != null && longitude != null) {
        latitude?.let { latitude ->
            longitude?.let { longitude ->
                Point(latitude, longitude)
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
