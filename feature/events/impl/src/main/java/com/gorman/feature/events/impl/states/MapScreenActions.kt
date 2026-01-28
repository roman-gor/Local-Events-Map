package com.gorman.feature.events.impl.states

import androidx.compose.runtime.Immutable
import com.gorman.common.constants.CityCoordinatesConstants
import com.yandex.mapkit.geometry.Point

@Immutable
data class MapScreenActions(
    val onCameraIdle: (Point?) -> Unit,
    val onSyncClick: () -> Unit,
    val onEventClick: (MapUiEvent) -> Unit,
    val onCitySubmit: (CityCoordinatesConstants) -> Unit,
    val onNavigateToDetailsScreen: (MapUiEvent) -> Unit,
    val filterActions: FilterActions
)

@Immutable
data class YandexMapActions(
    val onCameraIdle: (Point?) -> Unit,
    val onMarkerClick: (String) -> Unit
)
