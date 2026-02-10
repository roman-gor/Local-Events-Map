package com.gorman.feature.events.impl.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.gorman.common.constants.CityCoordinates
import com.gorman.common.data.NetworkConnectivityObserver
import com.gorman.common.models.CityData
import com.gorman.data.repository.geo.IGeoRepository
import com.gorman.data.repository.mapevents.IMapEventsRepository
import com.gorman.domainmodel.MapEvent
import com.gorman.domainmodel.PointDomain
import com.gorman.feature.details.api.DetailsScreenNavKey
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
import com.gorman.navigation.navigator.Navigator
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
import kotlinx.coroutines.flow.flow
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
    private val navigator: Navigator,
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

    private val cityData: Flow<CityData> = geoRepository.getSavedCity().map { it ?: CityData() }

    private data class UserInputState(
        val filters: FiltersState,
        val cityData: CityData,
        val selectedEventId: String?
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
        selectedEventId
    ) { filters, cityData, selectedEventId ->
        UserInputState(filters, cityData, selectedEventId)
    }

    private val isOutdated = flow {
        emit(mapEventsRepository.isOutdated())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val uiState: StateFlow<ScreenState> = combine(
        mapEventsRepository.getAllEvents(),
        userInputs,
        isNetworkAvailable
    ) { events, inputs, hasInternet ->
        val (filters, cityData, selectedEventId) = inputs

        val filteredEvents = filterEvents(events, filters, cityData, selectedEventId).toImmutableList()

        val status = when {
            !hasInternet -> DataStatus.OFFLINE
            isOutdated.value -> DataStatus.OUTDATED
            else -> DataStatus.FRESH
        }
        ScreenState.Success(
            eventsList = filteredEvents,
            filterState = filters,
            selectedMapEventId = selectedEventId,
            cityData = cityData.toUiState(),
            dataStatus = status
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
            is ScreenUiEvent.OnCameraIdle -> onCameraIdle(event.point)
            is ScreenUiEvent.OnCategoryChanged -> onCategoryChanged(event.category)
            is ScreenUiEvent.OnDateChanged -> { filterDateChanged(event.dateState) }
            is ScreenUiEvent.OnNameChanged -> filters.value = filters.value.copy(name = event.name)
            is ScreenUiEvent.OnCostChanged -> filters.value = filters.value.copy(isFree = event.isFree)
            is ScreenUiEvent.OnDistanceChanged -> filters.value = filters.value.copy(distance = event.distance)
            is ScreenUiEvent.OnCitySearch -> {
                searchForCity(event.city)
            }
            is ScreenUiEvent.OnEventSelected -> {
                viewModelScope.launch {
                    val currentId = selectedEventId.value
                    selectedEventId.value = if (currentId == event.id) null else event.id
                }
            }
            ScreenUiEvent.OnSyncClicked -> { viewModelScope.launch { syncEvents() } }
            ScreenUiEvent.PermissionsGranted -> {
                viewModelScope.launch {
                    fetchInitialLocation()
                    syncEvents()
                }
            }
            is ScreenUiEvent.OnNavigateToDetailsScreen -> {}
            is ScreenUiEvent.OnNavigateToDetailsScreen -> {
                navigator.goTo(DetailsScreenNavKey(event.event.id))
            }
        }
    }

    private suspend fun fetchInitialLocation() {
        if (isInitialLocationFetched) return
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
                Log.d("Date Check State", filters.value.dateRange.toString())
            }
            DateFilterType.TODAY -> {
                filters.value = filters.value.copy(
                    dateRange = DateFilterState(
                        type = dateState.type,
                        startDate = getStartOfDay(),
                        endDate = getEndOfDay()
                    )
                )
                Log.d("Date Check State", filters.value.dateRange.toString())
            }
            DateFilterType.WEEK -> {
                filters.value = filters.value.copy(
                    dateRange = DateFilterState(
                        type = dateState.type,
                        startDate = getStartOfDay(),
                        endDate = getEndOfWeek()
                    )
                )
                Log.d("Date Check State", filters.value.dateRange.toString())
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

    fun onCameraIdle(cameraPosition: PointUiState) {
        cameraMoveJob?.cancel()
        cameraMoveJob = viewModelScope.launch {
            delay(100L)
            val resultCityData = getCityByPointUseCase(cameraPosition.toDomain())
            resultCityData?.let {
                updateCityIfChanged(it)
            }
        }
    }

    fun searchForCity(city: CityCoordinates) {
        viewModelScope.launch {
            val resultCityData = getPointByCityUseCase(city)
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

    private suspend fun syncEvents() {
        try {
            mapEventsRepository.syncWith().getOrElse { exception ->
                _sideEffect.send(ScreenSideEffect.ShowToast(exception.message ?: "Error when sync worked"))
                Log.d("SyncError", "${exception.message}")
            }
        } catch (e: ApiException) {
            Log.e("ViewModel", "Error when sync events ${e.message}")
        }
    }

    private fun onCategoryChanged(category: String) {
        val currentCategories = filters.value.categories.toMutableList()
        if (currentCategories.contains(category)) {
            currentCategories.remove(category)
        } else {
            currentCategories.add(category)
        }
        Log.d("FilterList", filters.value.categories.toString())
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
