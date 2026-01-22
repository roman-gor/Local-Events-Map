package com.gorman.events.ui.states

import androidx.compose.runtime.Immutable
import com.gorman.common.constants.CityCoordinatesConstants
import com.gorman.ui.states.MapUiEvent
import com.yandex.mapkit.geometry.Point

@Immutable
data class MapScreenActions(
    val onCameraIdle: (Point?) -> Unit,
    val onSyncClick: () -> Unit,
    val onEventClick: (MapUiEvent) -> Unit,
    val onCitySubmit: (CityCoordinatesConstants) -> Unit,
    val filterActions: FilterActions
)

@Immutable
data class YandexMapActions(
    val onCameraIdle: (Point?) -> Unit,
    val onMarkerClick: (String) -> Unit
)
