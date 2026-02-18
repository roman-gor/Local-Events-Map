package com.gorman.feature.events.impl.ui.screens.mapscreen

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.gorman.common.constants.CityCoordinates
import com.gorman.feature.events.impl.R
import com.gorman.feature.events.impl.navigation.EventsNavDelegate
import com.gorman.feature.events.impl.ui.components.CitiesDropdownMenu
import com.gorman.feature.events.impl.ui.components.StatusBanner
import com.gorman.feature.events.impl.ui.mappers.toDomain
import com.gorman.feature.events.impl.ui.screens.PermissionRequestScreen
import com.gorman.feature.events.impl.ui.states.FilterActions
import com.gorman.feature.events.impl.ui.states.MapScreenActions
import com.gorman.feature.events.impl.ui.states.PointUiState
import com.gorman.feature.events.impl.ui.states.ScreenState
import com.gorman.feature.events.impl.ui.states.ScreenUiEvent
import com.gorman.feature.events.impl.ui.viewmodels.MapViewModel
import com.gorman.map.ui.LocalEventsMap
import com.gorman.map.ui.MapConfig
import com.gorman.map.ui.MapControl
import com.gorman.map.ui.MapMarker
import com.gorman.map.ui.rememberMapControl
import com.gorman.navigation.navigator.LocalNavigator
import com.gorman.ui.components.ErrorDataScreen
import com.gorman.ui.components.LoadingIndicator
import com.gorman.ui.theme.LocalEventsMapTheme
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

    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val mapControl = rememberMapControl()

    HandleSideEffects(context, mapViewModel, mapControl)

    BindPermissionLogic(
        permissionsState = locationPermissionsState,
        onPermissionsGranted = { mapViewModel.onUiEvent(ScreenUiEvent.PermissionsGranted) }
    )

    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is ScreenState.Error -> ErrorDataScreen(
            text = stringResource(com.gorman.ui.R.string.errorDataLoading),
            onRetryClick = {}
        )
        ScreenState.Loading -> LoadingIndicator()
        is ScreenState.CitySelection -> {
            PermissionRequestScreen(
                showManualInput = state.requiresManualInput,
                shouldShowRationale = locationPermissionsState.shouldShowRationale,
                requestPermissions = { locationPermissionsState.launchMultiplePermissionRequest() },
                onDeclineClick = { mapViewModel.onUiEvent(ScreenUiEvent.PermissionDenied) },
                onCitySubmit = { mapViewModel.onUiEvent(ScreenUiEvent.OnCitySearch(it)) },
                isPreRequest = !state.requiresManualInput && !locationPermissionsState.shouldShowRationale
            )
            if (locationPermissionsState.allPermissionsGranted) {
                LaunchedEffect(Unit) { mapViewModel.onUiEvent(ScreenUiEvent.PermissionsGranted) }
                LoadingIndicator()
                return
            }
        }
        is ScreenState.Success -> {
            MapContent(
                uiState = state,
                mapControl = mapControl,
                onUiEvent = mapViewModel::onUiEvent,
                modifier = modifier
            )
            RequestNotificationPermission()
        }
    }
}

@Composable
fun MapContent(
    uiState: ScreenState.Success,
    onUiEvent: (ScreenUiEvent) -> Unit,
    mapControl: MapControl,
    modifier: Modifier = Modifier
) {
    val navigator = EventsNavDelegate(LocalNavigator.current)
    LifecycleStartEffect(Unit) {
        onUiEvent(ScreenUiEvent.OnStart)
        onStopOrDispose {
            onUiEvent(ScreenUiEvent.OnStop)
        }
    }

    val state = rememberMapScreenLocalState()

    val (initialPoint, initialZoom) = uiState.initialCameraPosition

    val mapMarkers = remember(uiState.eventsList) {
        uiState.eventsList.mapNotNull { event ->
            val coordinates = event.coordinates?.split(",")
            if (coordinates != null && coordinates.size >= 2) {
                MapMarker(
                    id = event.id,
                    latitude = coordinates[0].trim().toDouble(),
                    longitude = coordinates[1].trim().toDouble(),
                    isSelected = event.isSelected,
                    iconRes = R.drawable.ic_marker,
                    selectedIconRes = R.drawable.ic_marker_selected
                )
            } else {
                null
            }
        }.toImmutableList()
    }

    val mapConfig = MapConfig(
        isDarkMode = state.isDarkMode,
        userLocation = uiState.cityData.cityCoordinates?.toDomain(),
        userLocationIconRes = R.drawable.ic_location_marker,
        initialPosition = initialPoint?.toDomain(),
        initialZoom = initialZoom
    )

    MapScreen(
        mapScreenActions = MapScreenActions(
            onCameraIdle = { location, zoom ->
                location?.let { onUiEvent(ScreenUiEvent.OnCameraIdle(location, zoom)) }
            },
            filterActions = FilterActions(
                onCategoryChange = { onUiEvent(ScreenUiEvent.OnCategoryChanged(it)) },
                onDateRangeChange = { onUiEvent(ScreenUiEvent.OnDateChanged(it)) },
                onDistanceChange = { onUiEvent(ScreenUiEvent.OnDistanceChanged(it)) },
                onCostChange = { onUiEvent(ScreenUiEvent.OnCostChanged(it)) },
                onNameChange = { onUiEvent(ScreenUiEvent.OnNameChanged(it)) }
            ),
            onSyncClick = { onUiEvent(ScreenUiEvent.OnSyncClicked) },
            onEventClick = { event -> onUiEvent(ScreenUiEvent.OnEventSelected(event.id)) },
            onCitySubmit = { city -> onUiEvent(ScreenUiEvent.OnCitySearch(city)) },
            onMapClick = { onUiEvent(ScreenUiEvent.OnMapClick) },
            onNavigateToDetailsScreen = { event -> navigator.navigateToDetails(event.id) }
        ),
        uiState = uiState,
        mapControl = mapControl,
        mapConfig = mapConfig,
        mapMarkers = mapMarkers,
        state = state,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    mapScreenActions: MapScreenActions,
    uiState: ScreenState.Success,
    mapControl: MapControl,
    mapConfig: MapConfig,
    mapMarkers: ImmutableList<MapMarker>,
    state: MapScreenLocalState,
    modifier: Modifier = Modifier
) {
    val selectedEvent = uiState.eventsList.firstOrNull { it.id == uiState.selectedMapEventId }

    Box(modifier = modifier) {
        LocalEventsMap(
            modifier = Modifier.fillMaxSize(),
            markers = mapMarkers,
            mapControl = mapControl,
            config = mapConfig,
            onCameraIdle = { lat, lon, zoom -> mapScreenActions.onCameraIdle(PointUiState(lat, lon), zoom) },
            onMapClick = { mapScreenActions.onMapClick() },
            onMarkerClick = { id -> uiState.eventsList.find { it.id == id }?.let { mapScreenActions.onEventClick(it) } }
        )
        MapTopOverlays(
            uiState = uiState,
            state = state,
            onCitySubmit = { mapScreenActions.onCitySubmit(it) }
        )
        MapBottomSheets(
            uiState = uiState,
            state = state,
            mapScreenActions = mapScreenActions
        )
        FunctionalBlock(
            mapScreenData = MapScreenData(
                name = uiState.filterState.name,
                selectedEvent = selectedEvent,
                isSyncLoading = uiState.isSyncLoading ?: false,
                listEventsButtonVerticalOffset = state.listEventsButtonOffset.value,
                filtersButtonVerticalOffset = state.filtersButtonOffset.value,
                mapScreenActions = mapScreenActions,
                onMapEventsListExpanded = { state.mapEventsListExpanded = !state.mapEventsListExpanded },
                onFiltersExpanded = { state.filtersExpanded = !state.filtersExpanded },
                isEventSelected = selectedEvent != null,
                onMapEventSelectedItemClick = { mapScreenActions.onNavigateToDetailsScreen(it) }
            )
        )
        MapEdgeGestureInterceptor(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .width(LocalEventsMapTheme.dimens.paddingExtraLarge)
        )
        MapEdgeGestureInterceptor(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(LocalEventsMapTheme.dimens.paddingExtraLarge)
        )
        MapEdgeGestureInterceptor(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(LocalEventsMapTheme.dimens.paddingExtraLarge)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MapBottomSheets(
    uiState: ScreenState.Success,
    state: MapScreenLocalState,
    mapScreenActions: MapScreenActions
) {
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
}

@Composable
private fun MapTopOverlays(
    uiState: ScreenState.Success,
    state: MapScreenLocalState,
    onCitySubmit: (CityCoordinates) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        uiState.cityData.city?.let {
            CitiesDropdownMenu(
                expanded = state.citiesMenuExpanded,
                onExpandedChange = { state.citiesMenuExpanded = !state.citiesMenuExpanded },
                currentCity = stringResource(it.resource),
                onCityClick = onCitySubmit,
                citiesList = CityCoordinates.entries.toImmutableList()
            )
        }
        uiState.dataStatus?.let { StatusBanner(it) }
    }
}

@Composable
private fun MapEdgeGestureInterceptor(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { },
                    onPress = { }
                )
            }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun BindPermissionLogic(
    permissionsState: MultiplePermissionsState,
    onPermissionsGranted: () -> Unit
) {
    val onPermissionsGrantedState by rememberUpdatedState(onPermissionsGranted)

    LaunchedEffect(permissionsState.allPermissionsGranted) {
        if (permissionsState.allPermissionsGranted) {
            onPermissionsGrantedState()
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun RequestNotificationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
        LaunchedEffect(Unit) {
            if (!notificationState.status.isGranted) {
                notificationState.launchPermissionRequest()
            }
        }
    }
}
