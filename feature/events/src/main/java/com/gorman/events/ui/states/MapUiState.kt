package com.gorman.events.ui.states

import com.gorman.domainmodel.MapEvent

data class MapUiState(
    val selectedMapEvent: MapEvent? = null,
    val filters: FiltersState = FiltersState(),
    val eventsList: List<MapEvent> = emptyList(),
    val coordinatesList: List<Pair<Double, Double>> = emptyList(),
    val cityData: CityData,
    val cityChanged: Boolean = false
)
