package com.gorman.events.ui.screens.mapscreen

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.PointF
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.gorman.common.constants.CityCoordinatesConstants
import com.gorman.events.R
import com.gorman.events.ui.components.CitiesDropdownMenu
import com.gorman.events.ui.screens.PermissionRequestScreen
import com.gorman.events.ui.states.FilterActions
import com.gorman.events.ui.states.MapScreenActions
import com.gorman.events.ui.states.ScreenState
import com.gorman.events.ui.states.ScreenUiEvent
import com.gorman.events.ui.states.YandexMapActions
import com.gorman.events.ui.utils.MapController
import com.gorman.events.ui.utils.rememberMapController
import com.gorman.events.ui.viewmodels.MapViewModel
import com.gorman.ui.components.ErrorDataScreen
import com.gorman.ui.components.LoadingStub
import com.gorman.ui.states.MapUiEvent
import com.gorman.ui.theme.LocalEventsMapTheme
import com.gorman.ui.utils.cityNameDefinition
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreenEntry(
    mapViewModel: MapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
        )
    )

    val mapController = rememberMapController()

    HandleSideEffects(context, mapViewModel, mapController)

    BindPermissionLogic(
        permissionsState = permissionsState,
        onPermissionsGranted = { mapViewModel.onUiEvent(ScreenUiEvent.PermissionsGranted) }
    )

    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()

    when (permissionsState.allPermissionsGranted) {
        true -> {
            MapContent(
                uiState = uiState,
                onUiEvent = mapViewModel::onUiEvent,
                mapController = mapController
            )
        }
        false -> {
            if (permissionsState.shouldShowRationale) {
                PermissionRequestScreen(
                    showManualInput = false,
                    onCitySubmit = { },
                    shouldShowRationale = true,
                    requestPermissions = { permissionsState.launchMultiplePermissionRequest() }
                )
            } else {
                val cityHasCoordinates = (uiState as? ScreenState.Success)?.cityData?.cityCoordinates != null

                if (!cityHasCoordinates) {
                    PermissionRequestScreen(
                        showManualInput = !permissionsState.shouldShowRationale,
                        onCitySubmit = { city ->
                            mapViewModel.onUiEvent(ScreenUiEvent.OnCitySearch(city))
                        },
                        shouldShowRationale = false,
                        requestPermissions = { permissionsState.launchMultiplePermissionRequest() }
                    )
                } else {
                    MapContent(
                        uiState = uiState,
                        onUiEvent = mapViewModel::onUiEvent,
                        mapController = mapController
                    )
                }
            }
        }
    }
}

@SuppressLint("ComposeModifierMissing")
@Composable
fun MapContent(
    uiState: ScreenState,
    onUiEvent: (ScreenUiEvent) -> Unit,
    mapController: MapController
) {
    val state = rememberMapScreenLocalState()
    when (uiState) {
        is ScreenState.Error -> ErrorDataScreen()
        ScreenState.Loading -> LoadingStub()
        is ScreenState.Success -> {
            MapScreen(
                mapScreenActions = MapScreenActions(
                    onCameraIdle = { location ->
                        location?.let {
                            onUiEvent(ScreenUiEvent.OnCameraIdle(location))
                        }
                    },
                    filterActions = FilterActions(
                        onCategoryChange = { category ->
                            onUiEvent(ScreenUiEvent.OnCategoryChanged(category))
                        },
                        onDateRangeChange = { dateState ->
                            onUiEvent(ScreenUiEvent.OnDateChanged(dateState))
                        },
                        onDistanceChange = { currentDistance ->
                            onUiEvent(ScreenUiEvent.OnDistanceChanged(currentDistance))
                        },
                        onCostChange = { isFree ->
                            onUiEvent(ScreenUiEvent.OnCostChanged(isFree))
                        },
                        onNameChange = { name ->
                            onUiEvent(ScreenUiEvent.OnNameChanged(name))
                        }
                    ),
                    onSyncClick = { onUiEvent(ScreenUiEvent.OnSyncClicked) },
                    onEventClick = { event ->
                        onUiEvent(ScreenUiEvent.OnEventSelected(event.id))
                        state.isEventSelected = event.id != uiState.selectedMapEventId
                    },
                    onCitySubmit = { city -> onUiEvent(ScreenUiEvent.OnCitySearch(city)) },
                ),
                uiState = uiState,
                mapController = mapController,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background),
                state = state
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    mapScreenActions: MapScreenActions,
    uiState: ScreenState.Success,
    mapController: MapController,
    state: MapScreenLocalState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        YandexMapView(
            mapController = mapController,
            yandexMapActions = YandexMapActions(
                onCameraIdle = mapScreenActions.onCameraIdle,
                onMarkerClick = { eventId ->
                    uiState.eventsList.find { it.id == eventId }?.let { mapScreenActions.onEventClick(it) }
                },
            ),
            isDarkMode = state.isDarkMode,
            eventsList = uiState.eventsList,
            initialCityPoint = uiState.cityData.cityCoordinates
        )
        uiState.cityData.city?.let {
            CitiesDropdownMenu(
                expanded = state.citiesMenuExpanded,
                onExpandedChange = { state.citiesMenuExpanded = !state.citiesMenuExpanded },
                currentCity = cityNameDefinition(it),
                onCityClick = { city -> mapScreenActions.onCitySubmit(city) },
                citiesList = CityCoordinatesConstants.cityCoordinatesList.toImmutableList()
            )
        }
        MapEventsBottomSheetContent(
            data = BottomSheetData(
                expanded = state.mapEventsListExpanded,
                onDismissSheet = { state.mapEventsListExpanded = !state.mapEventsListExpanded },
                sheetState = state.mapEventsListSheetState
            ),
            onEventClick = {
                mapScreenActions.onEventClick(it)
                state.scope.launch {
                    state.mapEventsListSheetState.hide()
                    state.mapEventsListExpanded = false
                }
            },
            eventsList = uiState.eventsList
        )
        FilterBottomSheetContent(
            data = BottomSheetData(
                expanded = state.filtersExpanded,
                onDismissSheet = { state.filtersExpanded = !state.filtersExpanded },
                sheetState = state.filtersSheetState
            ),
            filtersState = uiState.filterState,
            mapScreenActions = mapScreenActions
        )
        val selectedEvent = if (state.isEventSelected) {
            uiState.eventsList.firstOrNull { it.id == uiState.selectedMapEventId }
        } else { null }
        FunctionalBlock(
            mapScreenData = MapScreenData(
                name = uiState.filterState.name,
                selectedEvent = selectedEvent,
                listEventsButtonVerticalOffset = state.listEventsButtonOffset.value,
                filtersButtonVerticalOffset = state.filtersButtonOffset.value,
                mapScreenActions = mapScreenActions,
                onMapEventsListExpanded = { state.mapEventsListExpanded = !state.mapEventsListExpanded },
                onFiltersExpanded = { state.filtersExpanded = !state.filtersExpanded },
                isEventSelected = state.isEventSelected,
                onMapEventSelectedItemClick = { /*TODO(NavigationToDetailsScreen)*/ }
            )
        )
    }
}

@SuppressLint("ComposeModifierMissing")
@Composable
fun YandexMapView(
    mapController: MapController,
    yandexMapActions: YandexMapActions,
    isDarkMode: Boolean,
    eventsList: ImmutableList<MapUiEvent>,
    initialCityPoint: Point?
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    val normalIcon = remember { ImageProvider.fromResource(context, R.drawable.ic_marker) }
    val selectedIcon = remember {
        ImageProvider.fromResource(context, R.drawable.ic_marker_selected)
    }
    val userLocationIcon = remember {
        ImageProvider.fromResource(context, R.drawable.ic_location_marker)
    }

    val tapListener = remember(yandexMapActions.onMarkerClick) {
        MapObjectTapListener { mapObject, _ ->
            val eventId = mapObject.userData as? String
            if (eventId != null) yandexMapActions.onMarkerClick(eventId)
            true
        }
    }

    YandexMapEffects(
        mapView = mapView,
        mapController = mapController,
        yandexMapActions = yandexMapActions,
        initialCityPoint = initialCityPoint
    )

    AndroidView(
        factory = { mapView },
        modifier = Modifier
            .fillMaxSize()
            .clip(LocalEventsMapTheme.shapes.medium),
        update = {
            mapView.updateMapState(
                isDarkMode = isDarkMode,
                eventsList = eventsList,
                selectedIcon = selectedIcon,
                normalIcon = normalIcon,
                tapListener = tapListener
            )
            initialCityPoint?.let {
                mapView.mapWindow.map.mapObjects.addPlacemark().apply {
                    geometry = it
                    setIcon(userLocationIcon)
                }
            }
        }
    )
}

@Composable
private fun YandexMapEffects(
    mapView: MapView,
    mapController: MapController,
    yandexMapActions: YandexMapActions,
    initialCityPoint: Point?
) {
    DisposableEffect(mapView) {
        mapController.attach(mapView)
        mapView.onStart()
        MapKitFactory.getInstance().onStart()

        onDispose {
            mapView.onStop()
            MapKitFactory.getInstance().onStop()
            mapController.detach()
        }
    }
    DisposableEffect(mapView) {
        val cameraListener = CameraListener { _, _, reason, finished ->
            if (finished && reason == CameraUpdateReason.GESTURES) {
                yandexMapActions.onCameraIdle(mapView.mapWindow.map.cameraPosition.target)
            }
        }
        mapView.mapWindow.map.addCameraListener(cameraListener)
        onDispose {
            mapView.mapWindow.map.removeCameraListener(cameraListener)
        }
    }
    LaunchedEffect(Unit) {
        if (initialCityPoint != null) {
            mapView.mapWindow.map.move(
                CameraPosition(initialCityPoint, 11.0f, 0.0f, 0.0f)
            )
        }
    }
}

private fun MapView.updateMapState(
    isDarkMode: Boolean,
    eventsList: List<MapUiEvent>,
    selectedIcon: ImageProvider,
    normalIcon: ImageProvider,
    tapListener: MapObjectTapListener
) {
    this.mapWindow.map.isNightModeEnabled = isDarkMode
    val mapObjects = this.mapWindow.map.mapObjects
    mapObjects.clear()

    val iconStyle = IconStyle().apply {
        anchor = PointF(0.5f, 1.0f)
        zIndex = 10f
    }

    eventsList.forEach { event ->
        val coordinates = event.coordinates?.split(",")?.mapNotNull { it.trim().toDoubleOrNull() }
        if (coordinates != null && coordinates.size == 2) {
            val point = Point(coordinates[0], coordinates[1])
            mapObjects.addPlacemark().apply {
                geometry = point
                setIcon(if (event.isSelected) selectedIcon else normalIcon, iconStyle)
                userData = event.id
                addTapListener(tapListener)
            }
        }
    }

    val selectedEvent = eventsList.firstOrNull { it.isSelected }
    if (selectedEvent != null) {
        val coordinates = selectedEvent.coordinates?.split(",")?.mapNotNull { it.trim().toDoubleOrNull() }
        if (coordinates != null && coordinates.size == 2) {
            this.mapWindow.map.move(
                CameraPosition(Point(coordinates[0], coordinates[1]), 15.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 0.5f),
                null
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun BindPermissionLogic(
    permissionsState: MultiplePermissionsState,
    onPermissionsGranted: () -> Unit
) {
    LaunchedEffect(permissionsState.allPermissionsGranted) {
        if (permissionsState.allPermissionsGranted) {
            onPermissionsGranted()
        }
    }

    LaunchedEffect(Unit) {
        if (!permissionsState.allPermissionsGranted && !permissionsState.shouldShowRationale) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }
}
