package com.gorman.events.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.gorman.common.constants.CityCoordinatesConstants
import com.gorman.common.data.LocationProvider
import com.gorman.data.repository.IMapEventsRepository
import com.gorman.domainmodel.MapEvent
import com.gorman.events.ui.states.CityData
import com.gorman.events.ui.states.FiltersState
import com.gorman.events.ui.states.MapEventsState
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.Address
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.ToponymObjectMetadata
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.filter
import kotlin.collections.toMutableList

@HiltViewModel
class MapViewModel @Inject constructor(
    private val mapEventsRepository: IMapEventsRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {
    private val _mapEventsState = MutableStateFlow<MapEventsState>(MapEventsState.Idle)
    val mapEventState = _mapEventsState.asStateFlow()

    private val _filterState = MutableStateFlow(FiltersState())
    val filterState = _filterState.asStateFlow()

    private val _selectedMapEventId = MutableStateFlow<MapEvent?>(null)
    val selectedEventId = _selectedMapEventId.asStateFlow()

    private val _cityCenterData = MutableStateFlow(CityData())
    val cityCenterData = _cityCenterData.asStateFlow()

    private val _cityChanged = MutableStateFlow(false)
    val cityChanged = _cityChanged.asStateFlow()

    private var searchManager: SearchManager? = null
    private var searchSession: Session? = null
    private var cameraMoveJob: Job? = null
    private var lastCityEnum = CityCoordinatesConstants.MINSK

    init {
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    }

    fun fetchInitialLocation() {
        viewModelScope.launch {
            val location = locationProvider.getLastKnownLocation()
            if (location != null) {
                reverseGeocodeAndSetCity(location)
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
            reverseGeocodeAndSetCity(cameraPosition)
        }
    }

    private fun reverseGeocodeAndSetCity(location: Point) {
        val searchOptions = SearchOptions().apply {
            searchTypes = SearchType.GEO.value
            resultPageSize = 1
        }
        searchSession = searchManager?.submit(
            location,
            null,
            searchOptions,
            object : Session.SearchListener {
                override fun onSearchResponse(p0: Response) {
                    val geoObject = p0.collection.children.firstOrNull()?.obj
                        ?.metadataContainer
                        ?.getItem(ToponymObjectMetadata::class.java)
                    val city = geoObject?.address?.components
                        ?.firstOrNull {
                            it.kinds.contains(Address.Component.Kind.LOCALITY)
                        }?.name
                        ?: geoObject?.address?.components
                            ?.firstOrNull {
                                it.kinds.contains(Address.Component.Kind.AREA)
                            }?.name
                    Log.d("YandexGeoCoding", city.toString())
                    if (city != null) {
                        val currentCityName = _cityCenterData.value.city?.cityName

                        if (!currentCityName.equals(city, ignoreCase = true)) {
                            val cityEnum = CityCoordinatesConstants.fromCityName(city)

                            val newCityData = if (cityEnum != null) {
                                lastCityEnum = cityEnum
                                _cityChanged.value = true
                                CityData(city = cityEnum, cityCoordinates = location)
                            } else {
                                return
                            }

                            Log.d("YandexGeoCoding", "City Changed: true")
                            _cityCenterData.value = newCityData
                            getEventsList()
                        } else {
                            _cityChanged.value = false
                            Log.d("YandexGeoCoding", "City Changed: false")
                            return
                        }
                    }
                }

                override fun onSearchError(p0: com.yandex.runtime.Error) {
                    Log.e("MapViewModel", "Reverse geocoding error: $p0")
                }
            }
        )
    }

    fun searchForCity(city: CityCoordinatesConstants) {
        Log.d("Coordinates", "Запуск метода ${city.cityName}")
        val searchOptions = SearchOptions().apply {
            searchTypes = SearchType.GEO.value
            resultPageSize = 1
        }
        val boundingBox = BoundingBox(
            Point(49.0, 22.0),
            Point(57.0, 33.0)
        )
        searchSession = searchManager?.submit(
            city.cityName,
            Geometry.fromBoundingBox(boundingBox),
            searchOptions,
            object : Session.SearchListener {
                override fun onSearchResponse(response: Response) {
                    val firstGeoObject = response.collection.children.firstOrNull()?.obj
                    val point = firstGeoObject?.geometry?.first()?.point
                    _cityCenterData.value = CityData(city = city, cityCoordinates = point)
                    getEventsList()
                }

                override fun onSearchError(p0: com.yandex.runtime.Error) {
                    Log.e("MapViewModel", "Geocoding error: $p0")
                }
            }
        )
    }

    fun syncEvents() {
        _mapEventsState.value = MapEventsState.Loading
        viewModelScope.launch {
            try {
                mapEventsRepository.syncMapEvents()
                getEventsList()
            } catch (e: ApiException) {
                Log.e("ViewModel", "Error when sync events ${e.message}")
            }
        }
    }

    fun getEventsList() {
        _mapEventsState.value = MapEventsState.Loading
        viewModelScope.launch {
            mapEventsRepository.getAllLocalEvents()
                .combine(_filterState) { events, filters ->
                    Pair(events, filters)
                }.combine(_cityCenterData) { (events, filters), cityData ->
                    cityData.cityName?.let {
                        val eventsInCity = if (it.isNotBlank()) {
                            events.filter { event ->
                                event.city.equals(it, ignoreCase = true)
                            }
                        } else {
                            events
                        }
                        if (filters.categories.isEmpty()) {
                            eventsInCity
                        } else {
                            eventsInCity.filter { event ->
                                event.category in filters.categories
                            }
                        }
                    }
                }
                .collectLatest {
                    it?.let {
                        _mapEventsState.value = MapEventsState.Success(it)
                    }
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
