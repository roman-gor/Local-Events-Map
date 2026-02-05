package com.gorman.feature.events.impl.ui.states

import androidx.compose.runtime.Immutable
import com.gorman.common.constants.CityCoordinatesConstants

@Immutable
data class CityUiData(
    val city: CityCoordinatesConstants? = null,
    val cityName: String? = city?.cityName,
    val cityCoordinates: PointUiState? = null
)
