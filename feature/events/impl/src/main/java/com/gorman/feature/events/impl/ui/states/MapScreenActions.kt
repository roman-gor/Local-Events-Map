package com.gorman.feature.events.impl.ui.states

import androidx.compose.runtime.Immutable
import com.gorman.common.constants.CityCoordinates
import com.gorman.ui.states.MapUiEvent

@Immutable
data class MapScreenActions(
    val onCameraIdle: (PointUiState?, Float) -> Unit,
    val onSyncClick: () -> Unit,
    val onEventClick: (MapUiEvent) -> Unit,
    val onCitySubmit: (CityCoordinates) -> Unit,
    val onNavigateToDetailsScreen: (MapUiEvent) -> Unit,
    val filterActions: FilterActions,
    val onMapClick: () -> Unit
)
