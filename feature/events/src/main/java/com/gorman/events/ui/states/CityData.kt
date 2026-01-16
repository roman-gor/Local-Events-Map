package com.gorman.events.ui.states

import com.gorman.common.constants.CityCoordinatesConstants
import com.yandex.mapkit.geometry.Point

data class CityData(
    val city: CityCoordinatesConstants? = null,
    val cityName: String? = city?.cityName,
    val cityCoordinates: Point? = null
)
