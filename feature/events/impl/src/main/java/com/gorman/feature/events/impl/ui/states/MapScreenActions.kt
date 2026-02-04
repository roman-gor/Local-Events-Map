package com.gorman.feature.events.impl.ui.states

import androidx.compose.runtime.Immutable
import com.gorman.common.constants.CityCoordinatesConstants
import com.gorman.ui.states.MapUiEvent

@Immutable
data class MapScreenActions(
    val onCameraIdle: (PointUiState?) -> Unit,
    val onSyncClick: () -> Unit,
    val onEventClick: (MapUiEvent) -> Unit,
    val onCitySubmit: (CityCoordinatesConstants) -> Unit,
    val onNavigateToDetailsScreen: (MapUiEvent) -> Unit,
    val filterActions: FilterActions
)

@Immutable
data class YandexMapActions(
    val onCameraIdle: (PointUiState?) -> Unit,
    val onMarkerClick: (String) -> Unit
)
