package com.gorman.common.models

import com.gorman.common.constants.CityCoordinatesConstants
import kotlinx.serialization.Serializable

@Serializable
data class CityData(
    val city: CityCoordinatesConstants? = null,
    val cityName: String? = city?.cityName,
    val latitude: Double? = null,
    val longitude: Double? = null
)
