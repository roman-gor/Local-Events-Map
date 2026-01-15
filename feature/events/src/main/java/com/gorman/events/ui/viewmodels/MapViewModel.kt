package com.gorman.events.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.common.domain.usecases.GetAllMapEventsUseCase
import com.gorman.common.domain.usecases.SyncMapEventsFromRemoteUseCase
import com.gorman.domain_model.MapEvent
import com.gorman.events.ui.states.CityData
import com.gorman.events.ui.states.FiltersState
import com.gorman.events.ui.states.MapEventsState
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.toMutableList

@HiltViewModel
class MapViewModel @Inject constructor(
    private val syncMapEventsFromRemoteUseCase: SyncMapEventsFromRemoteUseCase,
    private val getAllMapEventsUseCase: GetAllMapEventsUseCase
): ViewModel() {
    private val _mapEventsState = MutableStateFlow<MapEventsState>(MapEventsState.Idle)
    val mapEventState = _mapEventsState.asStateFlow()

    private val _filterState = MutableStateFlow(FiltersState())
    val filterState = _filterState.asStateFlow()

    private val _selectedMapEventId = MutableStateFlow<MapEvent?>(null)
    val selectedEventId = _selectedMapEventId.asStateFlow()

    private val _cityCenterCoordinates = MutableStateFlow(CityData())
    val cityCenterCoordinates = _cityCenterCoordinates.asStateFlow()

    private var searchManager: SearchManager? = null
    private var searchSession: Session? = null

    init {
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    }

    fun searchForCity(cityName: String) {
        Log.d("Coordinates", "Запуск метода $cityName")
        val searchOptions = SearchOptions().apply {
            searchTypes = SearchType.GEO.value
            resultPageSize = 1
        }
        val boundingBox = BoundingBox(
            Point(49.0, 22.0),
            Point(57.0, 33.0))
        searchSession = searchManager?.submit(
            cityName,
            Geometry.fromBoundingBox(boundingBox),
            searchOptions,
            object : Session.SearchListener {
                override fun onSearchResponse(response: Response) {
                    val firstGeoObject = response.collection.children.firstOrNull()?.obj
                    val point = firstGeoObject?.geometry?.first()?.point
                    _cityCenterCoordinates.value = CityData(cityName, point)
                    getEventsList()
                }

                override fun onSearchError(p0: com.yandex.runtime.Error) {
                    Log.e("MapViewModel", "Geocoding error: $p0")
                }
            }
        )
    }

    fun syncEvents() {
        viewModelScope.launch {
            syncMapEventsFromRemoteUseCase()
        }
    }

    fun getEventsList() {
        _mapEventsState.value = MapEventsState.Loading
        viewModelScope.launch {
            getAllMapEventsUseCase()
                .onSuccess { eventsFlow ->
                    eventsFlow.combine(_filterState) { events, filters ->
                        if (filters.categories.isEmpty()) {
                            events
                        }
                        else {
                            events.filter { event ->
                                val categoryMatch = event.category in filters.categories
                                categoryMatch
                            }
                        }
                    }
                    .collectLatest {
                        _mapEventsState.value = MapEventsState.Success(it)
                    }
                }
                .onFailure { exception ->
                    _mapEventsState.value = MapEventsState.Error(exception)
                }
        }
    }

    fun onCategoryChanged(category: String) {
        val currentCategories = _filterState.value.categories.toMutableList()
        if (currentCategories.contains(category)) {
            currentCategories.remove(category)
        } else {
            currentCategories.add(category)
        }
        Log.d("FilterList", _filterState.value.categories.toString())
        _filterState.value = _filterState.value.copy(categories = currentCategories)
    }

    fun selectEvent(id: Int) {
        viewModelScope.launch {
            val state = _mapEventsState.value as MapEventsState.Success
            _selectedMapEventId.value = state.eventsList.first { it.localId == id }
        }
    }
}
