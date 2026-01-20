package com.gorman.events.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.gorman.common.constants.CityCoordinatesConstants
import com.gorman.common.models.CityData
import com.gorman.data.repository.IGeoRepository
import com.gorman.data.repository.IMapEventsRepository
import com.gorman.events.ui.components.DateFilterType
import com.gorman.events.ui.mappers.toUiState
import com.gorman.events.ui.states.DateFilterState
import com.gorman.events.ui.states.FiltersState
import com.gorman.events.ui.states.ScreenSideEffect
import com.gorman.events.ui.states.ScreenState
import com.gorman.events.ui.states.ScreenUiEvent
import com.gorman.events.ui.utils.getEndOfDay
import com.gorman.events.ui.utils.getEndOfWeek
import com.gorman.events.ui.utils.getStartOfDay
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
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
    private val _selectedEventId = MutableStateFlow<String?>(null)
    private var isInitialLocationFetched = false

    private var cameraMoveJob: Job? = null

    private val _cityData: Flow<CityData> = geoRepository.getSavedCity().map { it ?: CityData() }

    private val _sideEffect = Channel<ScreenSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    val uiState: StateFlow<ScreenState> = combine(
        mapEventsRepository.getAllEvents(),
        _filters,
        _cityData,
        _selectedEventId
    ) { events, filters, cityData, selectedEventId ->
        val filteredEvents = events.filter { event ->
            val matchesCity = cityData.cityName?.let {
                it.isBlank() || event.city.equals(it, ignoreCase = true)
            } ?: true

            val matchesCategory = filters.categories.isEmpty() || event.category in filters.categories

            val matchesDate = if (filters.dateRange.startDate != null && filters.dateRange.endDate != null) {
                event.date in filters.dateRange.startDate..filters.dateRange.endDate
            } else {
                true
            }

            val matchesName = event.name?.lowercase()?.contains(filters.name.lowercase()) ?: true

            matchesCity && matchesCategory && matchesDate && matchesName
        }.map { domainEvent ->
            domainEvent.toUiState().copy(isSelected = domainEvent.id == selectedEventId)
        }.toImmutableList()
        ScreenState.Success(
            eventsList = filteredEvents,
            filterState = filters,
            selectedMapEventId = selectedEventId,
            cityData = cityData.toUiState()
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
            is ScreenUiEvent.OnDateChanged -> { filterDateChanged(event.dateState) }
            is ScreenUiEvent.OnNameChanged -> { filterNameChanged(event.name) }
            is ScreenUiEvent.OnCitySearch -> searchForCity(event.city)
            is ScreenUiEvent.OnEventSelected -> selectEvent(event.id)
            ScreenUiEvent.OnSyncClicked -> syncEvents()
            ScreenUiEvent.PermissionsGranted -> {
                fetchInitialLocation()
                syncEvents()
            }
        }
    }

    private fun fetchInitialLocation() {
        if (isInitialLocationFetched) return
        isInitialLocationFetched = true

        viewModelScope.launch {
            val location = geoRepository.getUserLocation()
            if (location != null) {
                val userCityData = geoRepository.getCityByPoint(location)
                userCityData?.let { geoRepository.saveCity(it) }
                _sideEffect.send(ScreenSideEffect.MoveCamera(location))
            } else {
                searchForCity(CityCoordinatesConstants.MINSK)
            }
        }
    }

    private fun filterDateChanged(dateState: DateFilterState) {
        val currentType = _filters.value.dateRange.type
        val newType = dateState.type

        if (currentType == newType && newType != DateFilterType.RANGE) {
            resetDateFilter()
            return
        }

        if (newType == DateFilterType.RANGE && dateState.startDate == null) {
            return
        }

        when (dateState.type) {
            DateFilterType.RANGE -> {
                _filters.value = _filters.value.copy(
                    dateRange = DateFilterState(
                        type = DateFilterType.RANGE,
                        startDate = dateState.startDate,
                        endDate = dateState.endDate
                    )
                )
                Log.d("Date Check State", _filters.value.dateRange.toString())
            }
            DateFilterType.TODAY -> {
                _filters.value = _filters.value.copy(
                    dateRange = DateFilterState(
                        type = dateState.type,
                        startDate = getStartOfDay(),
                        endDate = getEndOfDay()
                    )
                )
                Log.d("Date Check State", _filters.value.dateRange.toString())
            }
            DateFilterType.WEEK -> {
                _filters.value = _filters.value.copy(
                    dateRange = DateFilterState(
                        type = dateState.type,
                        startDate = getStartOfDay(),
                        endDate = getEndOfWeek()
                    )
                )
                Log.d("Date Check State", _filters.value.dateRange.toString())
            }
            else -> resetDateFilter()
        }
    }

    private fun resetDateFilter() {
        _filters.value = _filters.value.copy(
            dateRange = DateFilterState(
                type = null,
                startDate = null,
                endDate = null
            )
        )
    }

    fun filterNameChanged(name: String) {
        _filters.value = _filters.value.copy(
            name = name
        )
    }

    fun onCameraIdle(cameraPosition: Point) {
        cameraMoveJob?.cancel()
        cameraMoveJob = viewModelScope.launch {
            delay(100L)
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
                geoRepository.saveCity(it)
                it.toUiState().cityCoordinates?.let { point ->
                    _sideEffect.send(ScreenSideEffect.MoveCamera(point))
                }
                Log.d(
                    "Coordinates City Choose",
                    "${it.toUiState().cityCoordinates?.latitude}" +
                        " / ${it.toUiState().cityCoordinates?.longitude}"
                )
                Log.d("Real Name City Choose", "${it.city?.cityName}")
                Log.d("Name City Choose", "${it.cityName}")
            }
        }
    }

    private fun syncEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mapEventsRepository.syncWith().getOrElse { exception ->
                    _sideEffect.send(ScreenSideEffect.ShowToast(exception.message ?: "Error when sync worked"))
                    Log.d("SyncError", "${exception.message}")
                }
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
        val currentState = uiState.value

        if (currentState !is ScreenState.Success) return

        val currentName = currentState.cityData.cityName
        val newName = newData.cityName

        if (currentName != newName && newName != null) {
            viewModelScope.launch {
                geoRepository.saveCity(newData)
            }
        }
    }
}
