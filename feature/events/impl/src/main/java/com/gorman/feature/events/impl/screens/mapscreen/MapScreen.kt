package com.gorman.feature.events.impl.screens.mapscreen

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.PointF
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.TextView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.gorman.common.constants.CityCoordinatesConstants
import com.gorman.common.constants.toDisplayName
import com.gorman.feature.events.impl.R
import com.gorman.feature.events.impl.components.CitiesDropdownMenu
import com.gorman.feature.events.impl.components.StatusBanner
import com.gorman.feature.events.impl.screens.PermissionRequestScreen
import com.gorman.feature.events.impl.states.FilterActions
import com.gorman.feature.events.impl.states.MapScreenActions
import com.gorman.feature.events.impl.states.ScreenState
import com.gorman.feature.events.impl.states.ScreenUiEvent
import com.gorman.feature.events.impl.states.YandexMapActions
import com.gorman.feature.events.impl.utils.MapController
import com.gorman.feature.events.impl.utils.rememberMapController
import com.gorman.feature.events.impl.viewmodels.MapViewModel
import com.gorman.ui.components.ErrorDataScreen
import com.gorman.ui.components.LoadingStub
import com.gorman.ui.states.MapUiEvent
import com.gorman.ui.theme.LocalEventsMapTheme
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.ClusterListener
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.ui_view.ViewProvider
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@SuppressLint("ComposeViewModelForwarding")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreenEntry(
    modifier: Modifier = Modifier,
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
                modifier = modifier,
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
                        modifier = modifier,
                        uiState = uiState,
                        onUiEvent = mapViewModel::onUiEvent,
                        mapController = mapController
                    )
                }
            }
        }
    }
}

@Composable
fun MapContent(
    uiState: ScreenState,
    onUiEvent: (ScreenUiEvent) -> Unit,
    mapController: MapController,
    modifier: Modifier = Modifier
) {
    LifecycleStartEffect(Unit) {
        onUiEvent(ScreenUiEvent.MapKitOnStart)
        onStopOrDispose {
            onUiEvent(ScreenUiEvent.MapKitOnStop)
        }
    }

    val state = rememberMapScreenLocalState()

    when (uiState) {
        is ScreenState.Error -> ErrorDataScreen(
            text = stringResource(com.gorman.ui.R.string.errorDataLoading),
            onRetryClick = {})
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
                    onNavigateToDetailsScreen = { event ->
                        onUiEvent(ScreenUiEvent.OnNavigateToDetailsScreen(event))
                    }
                ),
                uiState = uiState,
                mapController = mapController,
                modifier = modifier,
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
                    uiState.eventsList.find { it.id == eventId }
                        ?.let { mapScreenActions.onEventClick(it) }
                },
            ),
            isDarkMode = state.isDarkMode,
            eventsList = uiState.eventsList,
            initialCityPoint = uiState.cityData.cityCoordinates
        )
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            uiState.cityData.city?.let {
                CitiesDropdownMenu(
                    expanded = state.citiesMenuExpanded,
                    onExpandedChange = { state.citiesMenuExpanded = !state.citiesMenuExpanded },
                    currentCity = it.toDisplayName(),
                    onCityClick = { city -> mapScreenActions.onCitySubmit(city) },
                    citiesList = CityCoordinatesConstants.entries.toImmutableList()
                )
            }
            uiState.dataStatus?.let { StatusBanner(it) }
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
                onMapEventSelectedItemClick = { mapScreenActions.onNavigateToDetailsScreen(it) }
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

    val lastEventsList = remember { mutableStateOf<List<MapUiEvent>?>(null) }
    val lastDarkMode = remember { mutableStateOf<Boolean?>(null) }
    val lastSelectedEventId = remember { mutableStateOf<String?>(null) }

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
        update = { view ->
            val currentSelectedId = eventsList.find { it.isSelected }?.id

            val dataChanged = lastEventsList.value != eventsList
            val themeChanged = lastDarkMode.value != isDarkMode

            if (dataChanged || themeChanged) {
                view.updateMapState(
                    isDarkMode = isDarkMode,
                    eventsList = eventsList,
                    selectedIcon = selectedIcon,
                    normalIcon = normalIcon,
                    tapListener = tapListener
                )

                initialCityPoint?.let {
                    view.mapWindow.map.mapObjects.addPlacemark().apply {
                        geometry = it
                        setIcon(userLocationIcon)
                    }
                }

                lastEventsList.value = eventsList
                lastDarkMode.value = isDarkMode
            }

            if (currentSelectedId != lastSelectedEventId.value) {
                val selectedEvent = eventsList.firstOrNull { it.isSelected }
                if (selectedEvent != null) {
                    val coordinates = selectedEvent.coordinates?.split(",")?.mapNotNull { it.trim().toDoubleOrNull() }
                    if (coordinates != null && coordinates.size >= 2) {
                        view.mapWindow.map.move(
                            CameraPosition(Point(coordinates[0], coordinates[1]), 15.0f, 0.0f, 0.0f),
                            Animation(Animation.Type.SMOOTH, 0.5f),
                            null
                        )
                    }
                }
                lastSelectedEventId.value = currentSelectedId
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

        onDispose {
            mapView.onStop()
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
    val context = this.context
    this.mapWindow.map.isNightModeEnabled = isDarkMode
    val mapObjects = this.mapWindow.map.mapObjects
    mapObjects.clear()

    val clusterListener = ClusterListener { cluster ->
        val textView = TextView(context).apply {
            text = cluster.size.toString()
            textSize = 14f
            setTextColor(context.getColor(R.color.onPrimary))
            gravity = Gravity.CENTER

            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(context.getColor(R.color.primary))
                setSize(100, 100)
            }

            setPadding(20, 20, 20, 20)
        }

        cluster.appearance.setView(ViewProvider(textView))
        cluster.appearance.zIndex = 100f

        cluster.addClusterTapListener { _ ->
            val target = cluster.appearance.geometry
            this.mapWindow.map.move(
                CameraPosition(target, this.mapWindow.map.cameraPosition.zoom + 2, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 0.5f),
                null
            )
            true
        }
    }

    val clusterizedCollection = mapObjects.addClusterizedPlacemarkCollection(clusterListener)

    val iconStyle = IconStyle().apply {
        anchor = PointF(0.5f, 1.0f)
        zIndex = 10f
    }

    eventsList.forEach { event ->
        val coordinates = event.coordinates?.split(",")?.mapNotNull { it.trim().toDoubleOrNull() }
        if (coordinates != null && coordinates.size >= 2) {
            val point = Point(coordinates[0], coordinates[1])

            clusterizedCollection.addPlacemark().apply {
                geometry = point
                setIcon(if (event.isSelected) selectedIcon else normalIcon, iconStyle)
                userData = event.id
                addTapListener(tapListener)
            }
        }
    }

    clusterizedCollection.clusterPlacemarks(60.0, 15)

    val selectedEvent = eventsList.firstOrNull { it.isSelected }
    if (selectedEvent != null) {
        val coordinates = selectedEvent.coordinates?.split(",")?.mapNotNull { it.trim().toDoubleOrNull() }
        if (coordinates != null && coordinates.size >= 2) {
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
