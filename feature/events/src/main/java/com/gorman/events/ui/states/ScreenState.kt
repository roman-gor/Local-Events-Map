package com.gorman.events.ui.states

import com.gorman.common.constants.CityCoordinatesConstants
import com.yandex.mapkit.geometry.Point
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed interface ScreenState {
    object Loading : ScreenState
    data class Error(val throwable: Throwable) : ScreenState
    data class Success(
        val eventsList: ImmutableList<MapUiEvent> = persistentListOf(),
        val filterState: FiltersState = FiltersState(),
        val selectedMapEventId: String? = null,
        val cityData: CityUiData = CityUiData()
    ) : ScreenState
}

sealed interface ScreenUiEvent {
    data object PermissionsGranted : ScreenUiEvent
    data class PermissionsDenied(val manualCitySearch: Boolean) : ScreenUiEvent

    data class OnCameraIdle(val point: Point) : ScreenUiEvent
    data class OnCitySearch(val city: CityCoordinatesConstants) : ScreenUiEvent

    data object OnSyncClicked : ScreenUiEvent
    data class OnCategoryChanged(val category: String) : ScreenUiEvent
    data class OnEventSelected(val id: String) : ScreenUiEvent
}
