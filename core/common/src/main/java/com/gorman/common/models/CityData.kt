package com.gorman.common.models

import androidx.compose.runtime.Immutable
import com.gorman.common.constants.CityCoordinatesConstants
import com.yandex.mapkit.geometry.Point

@Immutable
data class CityData(
    val city: CityCoordinatesConstants? = null,
    val cityName: String? = city?.cityName,
    val cityCoordinates: Point? = null
)
