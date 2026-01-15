package com.gorman.events.ui.states

import com.yandex.mapkit.geometry.Point

data class CityData(
    val cityName: String = "",
    val cityCoordinates: Point? = null
)
