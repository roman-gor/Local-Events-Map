package com.gorman.feature.events.impl.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.common.constants.CityCoordinates
import com.gorman.common.data.NetworkConnectivityObserver
import com.gorman.common.models.CityData
import com.gorman.data.repository.geo.IGeoRepository
import com.gorman.data.repository.mapevents.IMapEventsRepository
import com.gorman.domainmodel.MapEvent
import com.gorman.domainmodel.PointDomain
import com.gorman.feature.events.impl.domain.GetCityByPointUseCase
import com.gorman.feature.events.impl.domain.GetPointByCityUseCase
import com.gorman.feature.events.impl.ui.components.DateFilterType
import com.gorman.feature.events.impl.ui.mappers.toDomain
import com.gorman.feature.events.impl.ui.mappers.toUiState
import com.gorman.feature.events.impl.ui.states.DataStatus
import com.gorman.feature.events.impl.ui.states.DateFilterState
import com.gorman.feature.events.impl.ui.states.FiltersState
import com.gorman.feature.events.impl.ui.states.PointUiState
import com.gorman.feature.events.impl.ui.states.ScreenSideEffect
import com.gorman.feature.events.impl.ui.states.ScreenState
import com.gorman.feature.events.impl.ui.states.ScreenUiEvent
import com.gorman.map.mapmanager.IMapManager
import com.gorman.ui.mappers.toUiState
import com.gorman.ui.states.MapUiEvent
import com.gorman.ui.utils.getEndOfDay
import com.gorman.ui.utils.getEndOfWeek
import com.gorman.ui.utils.getStartOfDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
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
import kotlin.ranges.contains

@Suppress("TooManyFunctions")
@HiltViewModel
class MapViewModel @Inject constructor(
    private val mapManager: IMapManager,
    private val mapEventsRepository: IMapEventsRepository,
    private val geoRepository: IGeoRepository,
    private val getCityByPointUseCase: GetCityByPointUseCase,
    private val getPointByCityUseCase: GetPointByCityUseCase,
    networkObserver: NetworkConnectivityObserver
) : ViewModel() {

    private val _sideEffect = Channel<ScreenSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    private val filters = MutableStateFlow(FiltersState())
    private val selectedEventId = MutableStateFlow<String?>(null)
    private var isInitialLocationFetched = false
    private var cameraMoveJob: Job? = null
    private val isPermissionDeclined = MutableStateFlow(false)
    private val cityData: Flow<CityData> = geoRepository.getSavedCity().map { it ?: CityData() }
    private val isSyncLoading = MutableStateFlow<Boolean?>(null)
    private var isInitialized = false
    private val cameraState = MutableStateFlow<Pair<PointDomain, Float>?>(null)

    private data class UserInputState(
        val filters: FiltersState,
        val cityData: CityData,
        val selectedEventId: String?,
        val isSyncLoading: Boolean?,
        val isPermissionDeclined: Boolean
    )

    private val isNetworkAvailable = networkObserver.observe()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    private val userInputs = combine(
        filters,
        cityData,
        selectedEventId,
        isSyncLoading,
        isPermissionDeclined
    ) { filters, cityData, selectedEventId, isSyncLoading, isPermissionDeclined ->
        UserInputState(filters, cityData, selectedEventId, isSyncLoading, isPermissionDeclined)
    }

    private val isOutdated = mapEventsRepository.isOutdated()

    val uiState: StateFlow<ScreenState> = combine(
        mapEventsRepository.getAllEvents(),
        userInputs,
        isNetworkAvailable,
        isOutdated,
        cameraState
    ) { events, inputs, hasInternet, isOutdated, cameraState ->
        val (filters, cityData, selectedEventId, isSyncLoading, isPermissionDeclined) = inputs
        if (cityData.cityName == null || cityData.city == null) {
            return@combine ScreenState.CitySelection(isPermissionDeclined)
        }

        val filteredEvents = filterEvents(events, filters, cityData, selectedEventId).toImmutableList()

        val status = when {
            !hasInternet -> DataStatus.OFFLINE
            isOutdated -> DataStatus.OUTDATED
            else -> DataStatus.FRESH
        }
        ScreenState.Success(
            eventsList = filteredEvents,
            filterState = filters,
            selectedMapEventId = selectedEventId,
            cityData = cityData.toUiState(),
            dataStatus = status,
            isSyncLoading = isSyncLoading,
            initialCameraPosition = cameraState?.let { it.first.toUiState() to it.second } ?: (null to null)
        ) as ScreenState
    }.catch { e ->
        emit(ScreenState.Error(e))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ScreenState.Loading
    )

    private fun filterEvents(
        events: List<MapEvent>,
        filters: FiltersState,
        cityData: CityData,
        selectedEventId: String?
    ): List<MapUiEvent> {
        return events.filter { event ->
            event.filter(filters, cityData)
        }.map { domainEvent ->
            domainEvent.toUiState().copy(isSelected = domainEvent.id == selectedEventId)
        }
    }

    private fun MapEvent.filter(
        filters: FiltersState,
        cityData: CityData
    ): Boolean {
        val matchesCity = cityData.cityName?.let {
            it.isBlank() || this.city.equals(it, ignoreCase = true)
        } ?: true

        val matchesCategory = filters.categories.isEmpty() || this.category in filters.categories

        val matchesDate = if (filters.dateRange.startDate != null && filters.dateRange.endDate != null) {
            this.date in filters.dateRange.startDate..filters.dateRange.endDate
        } else {
            true
        }

        val matchesName = this.name?.lowercase()?.contains(filters.name.lowercase()) ?: true

        val matchesDistance = if (filters.distance != null) {
            val userPoint = cityData.toUiState().cityCoordinates
            val dist = checkDistanceBetween(this.toUiState(), userPoint?.toDomain())
            dist?.let { it <= filters.distance } ?: true
        } else {
            true
        }

        val matchesIsFree = if (filters.isFree) this.price == 0 else true

        return matchesCity && matchesCategory && matchesDate && matchesName && matchesDistance && matchesIsFree
    }

    fun onUiEvent(event: ScreenUiEvent) {
        when (event) {
            ScreenUiEvent.OnStart -> mapManager.onStart()
            ScreenUiEvent.OnStop -> mapManager.onStop()
            is ScreenUiEvent.OnCameraIdle -> onCameraIdle(event.point, event.zoom)
            is ScreenUiEvent.OnCategoryChanged -> onCategoryChanged(event.category)
            is ScreenUiEvent.OnDateChanged -> { filterDateChanged(event.dateState) }
            is ScreenUiEvent.OnNameChanged -> filters.value = filters.value.copy(name = event.name)
            is ScreenUiEvent.OnCostChanged -> filters.value = filters.value.copy(isFree = event.isFree)
            is ScreenUiEvent.OnDistanceChanged -> filters.value = filters.value.copy(distance = event.distance)
            is ScreenUiEvent.OnCitySearch -> {
                searchForCity(event.city)
            }
            is ScreenUiEvent.OnEventSelected -> { viewModelScope.launch { onEventSelected(event.id) } }
            ScreenUiEvent.OnSyncClicked -> { viewModelScope.launch { syncEvents() } }
            ScreenUiEvent.PermissionsGranted -> {
                viewModelScope.launch {
                    isPermissionDeclined.value = false
                    if (!isInitialized) {
                        isInitialized = true
                        launch { fetchInitialLocation() }
                        launch { syncEvents() }
                    } else {
                        fetchInitialLocation()
                    }
                }
            }
            ScreenUiEvent.PermissionDenied -> {
                isPermissionDeclined.value = true
            }
            ScreenUiEvent.OnMapClick -> { selectedEventId.value = null }
        }
    }

    private suspend fun onEventSelected(id: String) {
        val currentId = selectedEventId.value
        if (currentId != id) {
            selectedEventId.value = id
            val events = (uiState.value as ScreenState.Success).eventsList
            val eventObj = events.find { it.id == id }
            val coords = eventObj?.coordinates?.split(",")
            if (coords != null && coords.size >= 2) {
                val point = PointDomain(coords[0].trim().toDouble(), coords[1].trim().toDouble())
                cameraState.value = point to 15f
                _sideEffect.send(ScreenSideEffect.MoveCamera(point.toUiState(), 15f))
            }
        } else {
            selectedEventId.value = null
        }
    }

    private suspend fun fetchInitialLocation() {
        if (isInitialLocationFetched || cameraState.value != null) return

        isInitialLocationFetched = true
        geoRepository.getUserLocation().onSuccess { location ->
            val userCityData = getCityByPointUseCase(location)
            userCityData?.let { geoRepository.saveCity(it) }
            _sideEffect.send(ScreenSideEffect.MoveCamera(location.toUiState()))
        }.onFailure {
            searchForCity(CityCoordinates.MINSK)
        }
    }

    private fun filterDateChanged(dateState: DateFilterState) {
        val currentType = filters.value.dateRange.type
        val newType = dateState.type

        if (currentType == newType && newType != DateFilterType.RANGE) {
            filters.value = filters.value.copy(
                dateRange = DateFilterState(
                    type = null,
                    startDate = null,
                    endDate = null
                )
            )
            return
        }

        if (newType == DateFilterType.RANGE && dateState.startDate == null) {
            return
        }

        when (dateState.type) {
            DateFilterType.RANGE -> {
                filters.value = filters.value.copy(
                    dateRange = DateFilterState(
                        type = DateFilterType.RANGE,
                        startDate = dateState.startDate,
                        endDate = dateState.endDate
                    )
                )
            }
            DateFilterType.TODAY -> {
                filters.value = filters.value.copy(
                    dateRange = DateFilterState(
                        type = dateState.type,
                        startDate = getStartOfDay(),
                        endDate = getEndOfDay()
                    )
                )
            }
            DateFilterType.WEEK -> {
                filters.value = filters.value.copy(
                    dateRange = DateFilterState(
                        type = dateState.type,
                        startDate = getStartOfDay(),
                        endDate = getEndOfWeek()
                    )
                )
            }
            else -> filters.value = filters.value.copy(
                dateRange = DateFilterState(
                    type = null,
                    startDate = null,
                    endDate = null
                )
            )
        }
    }

    private fun checkDistanceBetween(event: MapUiEvent, userLocation: PointDomain?): Int? {
        val eventLocation = event.coordinates?.let { coordinates ->
            val coordinatesList = coordinates.split(",")
            if (coordinatesList.size >= 2) {
                PointDomain(coordinatesList[0].toDouble(), coordinatesList[1].toDouble())
            } else {
                null
            }
        }

        return if (userLocation != null && eventLocation != null) {
            geoRepository.getDistanceFromPoints(userLocation, eventLocation)
        } else {
            null
        }
    }

    private fun onCameraIdle(cameraPosition: PointUiState, zoom: Float) {
        cameraState.value = cameraPosition.toDomain() to zoom

        cameraMoveJob?.cancel()
        cameraMoveJob = viewModelScope.launch {
            delay(100L)
            val resultCityData = getCityByPointUseCase(cameraPosition.toDomain())
            resultCityData?.let {
                updateCityIfChanged(it)
            }
        }
    }

    private fun searchForCity(city: CityCoordinates) {
        viewModelScope.launch {
            val resultCityData = getPointByCityUseCase(city)
            resultCityData?.let {
                geoRepository.saveCity(it)
                it.toUiState().cityCoordinates?.let { point ->
                    cameraState.value = point.toDomain() to 11f
                    _sideEffect.send(ScreenSideEffect.MoveCamera(point))
                }
            }
        }
    }

    private suspend fun syncEvents() {
        isSyncLoading.value = true
        mapEventsRepository.syncWith().onSuccess {
            isSyncLoading.value = false
        }.onFailure { exception ->
            isSyncLoading.value = false
            _sideEffect.send(ScreenSideEffect.ShowToast(exception.message ?: "Error when sync worked"))
        }
    }

    private fun onCategoryChanged(category: String) {
        val currentCategories = filters.value.categories.toMutableList()
        if (currentCategories.contains(category)) {
            currentCategories.remove(category)
        } else {
            currentCategories.add(category)
        }
        filters.value = filters.value.copy(categories = currentCategories)
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
