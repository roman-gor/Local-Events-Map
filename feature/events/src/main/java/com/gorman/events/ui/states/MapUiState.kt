package com.gorman.events.ui.states

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class MapUiState(
    val filters: FiltersState = FiltersState(),
    val eventsList: ImmutableList<MapUiEvent> = persistentListOf(),
    val cityData: CityData,
    val cityChanged: Boolean = false
)
