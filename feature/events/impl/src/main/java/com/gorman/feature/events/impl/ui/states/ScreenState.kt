package com.gorman.feature.events.impl.ui.states

import com.gorman.common.constants.CityCoordinatesConstants
import com.gorman.ui.states.MapUiEvent
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed interface ScreenState {
    object Loading : ScreenState
    data class Error(val throwable: Throwable) : ScreenState
    data class Success(
        val eventsList: ImmutableList<MapUiEvent> = persistentListOf(),
        val filterState: FiltersState = FiltersState(),
        val selectedMapEventId: String? = null,
        val cityData: CityUiData = CityUiData(),
        val dataStatus: DataStatus? = null,
        val isPermissionsRequested: Boolean? = null
    ) : ScreenState
}

sealed interface ScreenUiEvent {

    data object MapKitOnStart : ScreenUiEvent
    data object MapKitOnStop : ScreenUiEvent

    data object PermissionsGranted : ScreenUiEvent
    data object PermissionsRequested : ScreenUiEvent

    data class OnCameraIdle(val point: PointUiState) : ScreenUiEvent
    data class OnCitySearch(val city: CityCoordinatesConstants) : ScreenUiEvent

    data object OnSyncClicked : ScreenUiEvent
    data class OnCategoryChanged(val category: String) : ScreenUiEvent
    data class OnDateChanged(val dateState: DateFilterState) : ScreenUiEvent
    data class OnNameChanged(val name: String) : ScreenUiEvent
    data class OnCostChanged(val isFree: Boolean) : ScreenUiEvent
    data class OnDistanceChanged(val distance: Int?) : ScreenUiEvent
    data class OnEventSelected(val id: String) : ScreenUiEvent
    data class OnNavigateToDetailsScreen(val event: MapUiEvent) : ScreenUiEvent
}
