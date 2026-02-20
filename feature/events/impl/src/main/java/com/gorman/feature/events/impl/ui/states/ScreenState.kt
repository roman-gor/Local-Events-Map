package com.gorman.feature.events.impl.ui.states

import com.gorman.common.constants.CityCoordinates
import com.gorman.common.models.DateFilterState
import com.gorman.common.models.FiltersState
import com.gorman.ui.states.MapUiEvent
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed interface ScreenState {
    object Loading : ScreenState
    data class Error(val throwable: Throwable) : ScreenState
    data class CitySelection(
        val requiresManualInput: Boolean,
        val isLoading: Boolean
    ) : ScreenState
    data class Success(
        val eventsList: ImmutableList<MapUiEvent> = persistentListOf(),
        val filterState: FiltersState = FiltersState(),
        val selectedMapEventId: String? = null,
        val cityData: CityUiData = CityUiData(),
        val dataStatus: DataStatus? = null,
        val isSyncLoading: Boolean? = null,
        val initialCameraPosition: Pair<PointUiState?, Float?>
    ) : ScreenState
}

sealed interface ScreenUiEvent {

    data object OnStart : ScreenUiEvent
    data object OnStop : ScreenUiEvent

    data object PermissionsGranted : ScreenUiEvent
    data object PermissionDenied : ScreenUiEvent

    data class OnCameraIdle(val point: PointUiState, val zoom: Float) : ScreenUiEvent
    data class OnCitySearch(val city: CityCoordinates) : ScreenUiEvent
    object OnMapClick : ScreenUiEvent

    data object OnSyncClicked : ScreenUiEvent
    data class OnCategoryChanged(val category: String) : ScreenUiEvent
    data class OnDateChanged(val dateState: DateFilterState) : ScreenUiEvent
    data class OnNameChanged(val name: String) : ScreenUiEvent
    data class OnCostChanged(val isFree: Boolean) : ScreenUiEvent
    data class OnDistanceChanged(val distance: Int?) : ScreenUiEvent
    data object OnResetFilters : ScreenUiEvent
    data class OnEventSelected(val id: String) : ScreenUiEvent
}
