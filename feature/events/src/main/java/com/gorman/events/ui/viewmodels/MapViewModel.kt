package com.gorman.events.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.gorman.common.constants.CityCoordinatesConstants
import com.gorman.common.models.CityData
import com.gorman.data.repository.IGeoRepository
import com.gorman.data.repository.IMapEventsRepository
import com.gorman.events.ui.mappers.toUiState
import com.gorman.events.ui.states.FiltersState
import com.gorman.events.ui.states.ScreenState
import com.gorman.events.ui.states.ScreenUiEvent
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.filter
import kotlin.collections.toMutableList

@HiltViewModel
class MapViewModel @Inject constructor(
    private val mapEventsRepository: IMapEventsRepository,
    private val geoRepository: IGeoRepository
) : ViewModel() {

    private val _filters = MutableStateFlow(FiltersState())
    private val _cityData = MutableStateFlow(CityData())
    private val _selectedEventId = MutableStateFlow<String?>(null)
    private val _isCityChanged = MutableStateFlow(false)
    private var cameraMoveJob: Job? = null

    val uiState: StateFlow<ScreenState> = combine(
        mapEventsRepository.getAllEvents(),
        _filters,
        _cityData,
        _selectedEventId,
        _isCityChanged
    ) { events, filters, cityData, selectedEventId, isCityChanged ->
        val filteredEvents = events.filter { event ->
            val matchesCity = cityData.cityName?.let {
                it.isBlank() || event.city.equals(it, ignoreCase = true)
            } ?: true

            val matchesCategory = filters.categories.isEmpty() || event.category in filters.categories

            matchesCity && matchesCategory
        }.map { domainEvent ->
            domainEvent.toUiState().copy(isSelected = domainEvent.id == selectedEventId)
        }.toImmutableList()
        ScreenState.Success(
            eventsList = filteredEvents,
            filterState = filters,
            selectedMapEventId = selectedEventId,
            cityCenterData = cityData,
            isCityChanged = isCityChanged
        ) as ScreenState
    }.catch { e ->
        emit(ScreenState.Error(e))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ScreenState.Loading
    )

    fun onUiEvent(event: ScreenUiEvent) {
        when (event) {
            is ScreenUiEvent.OnCameraIdle -> onCameraIdle(event.point)
            is ScreenUiEvent.OnCategoryChanged -> onCategoryChanged(event.category)
            is ScreenUiEvent.OnCitySearch -> searchForCity(event.city)
            is ScreenUiEvent.OnEventSelected -> selectEvent(event.id)
            ScreenUiEvent.OnSyncClicked -> syncEvents()
            is ScreenUiEvent.PermissionsDenied -> TODO()
            ScreenUiEvent.PermissionsGranted -> TODO()
        }
    }

    fun fetchInitialLocation() {
        viewModelScope.launch {
            val location = geoRepository.getUserLocation()
            if (location != null) {
                geoRepository.getCityByPoint(location)
                Log.d("Location", "Find location: $location")
            } else {
                searchForCity(CityCoordinatesConstants.MINSK)
            }
        }
    }

    fun onCameraIdle(cameraPosition: Point) {
        cameraMoveJob?.cancel()
        cameraMoveJob = viewModelScope.launch {
            delay(2000L)
            val resultCityData = geoRepository.getCityByPoint(cameraPosition)
            resultCityData?.let {
                updateCityIfChanged(it)
            }
        }
    }

    fun searchForCity(city: CityCoordinatesConstants) {
        viewModelScope.launch {
            val resultCityData = geoRepository.getPointByCity(city)
            resultCityData?.let {
                _cityData.value = it
                _isCityChanged.value = true
                launch {
                    delay(100)
                    _isCityChanged.value = false
                }
            }
        }
    }

    private fun syncEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mapEventsRepository.syncWith()
            } catch (e: ApiException) {
                Log.e("ViewModel", "Error when sync events ${e.message}")
            }
        }
    }

    private fun onCategoryChanged(category: String) {
        val currentCategories = _filters.value.categories.toMutableList()
        if (currentCategories.contains(category)) {
            currentCategories.remove(category)
        } else {
            currentCategories.add(category)
        }
        Log.d("FilterList", _filters.value.categories.toString())
        _filters.value = _filters.value.copy(categories = currentCategories)
    }

    private fun selectEvent(id: String) {
        viewModelScope.launch {
            val currentId = _selectedEventId.value
            _selectedEventId.value = if (currentId == id) null else id
        }
    }

    private fun updateCityIfChanged(newData: CityData) {
        val currentName = _cityData.value.cityName
        val newName = newData.cityName

        if (currentName != newName && newName != null) {
            _cityData.value = newData
            _isCityChanged.value = true
            viewModelScope.launch {
                delay(100)
                _isCityChanged.value = false
            }
        }
    }
}
