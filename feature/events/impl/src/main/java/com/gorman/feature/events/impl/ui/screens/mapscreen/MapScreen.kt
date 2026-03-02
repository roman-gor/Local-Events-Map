package com.gorman.feature.events.impl.ui.screens.mapscreen

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.gorman.common.constants.CityCoordinates
import com.gorman.common.models.FilterActions
import com.gorman.feature.events.impl.R
import com.gorman.feature.events.impl.navigation.EventsNavDelegate
import com.gorman.feature.events.impl.ui.components.CitiesDropdownMenu
import com.gorman.feature.events.impl.ui.components.StatusBanner
import com.gorman.feature.events.impl.ui.mappers.toDomain
import com.gorman.feature.events.impl.ui.screens.PermissionRequestScreen
import com.gorman.feature.events.impl.ui.states.MapScreenActions
import com.gorman.feature.events.impl.ui.states.PointUiState
import com.gorman.feature.events.impl.ui.states.ScreenSideEffect
import com.gorman.feature.events.impl.ui.states.ScreenState
import com.gorman.feature.events.impl.ui.states.ScreenUiEvent
import com.gorman.feature.events.impl.ui.viewmodels.MapViewModel
import com.gorman.map.ui.LocalEventsMapComponent
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

    LaunchedEffect(mapViewModel.sideEffect) {
        mapViewModel.sideEffect.collect { effect ->
            when (effect) {
                is ScreenSideEffect.MoveCamera -> {
                    val zoom = effect.zoom
                    mapControl.moveCamera(
                        point = effect.point.toDomain(),
                        zoom = zoom
                    )
                }
                is ScreenSideEffect.ShowToast -> {
                    Toast.makeText(context, effect.text, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    BindPermissionLogic(
        permissionsState = locationPermissionsState,
        onPermissionsGranted = { mapViewModel.onUiEvent(ScreenUiEvent.PermissionsGranted) }
    )

    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is ScreenState.Error -> ErrorDataScreen(
            text = stringResource(com.gorman.ui.R.string.errorDataLoading),
            onRetryClick = {},
            modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)
        )
        ScreenState.Loading -> LoadingIndicator(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        )
        is ScreenState.CitySelection -> {
            if (state.isLoading) {
                LoadingIndicator(
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
                )
                return
            }
            PermissionRequestScreen(
                showManualInput = state.requiresManualInput,
                shouldShowRationale = locationPermissionsState.shouldShowRationale,
                requestPermissions = { locationPermissionsState.launchMultiplePermissionRequest() },
                onDeclineClick = { mapViewModel.onUiEvent(ScreenUiEvent.PermissionDenied) },
                onCitySubmit = { mapViewModel.onUiEvent(ScreenUiEvent.OnCitySearch(it)) },
                isPreRequest = !state.requiresManualInput && !locationPermissionsState.shouldShowRationale,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(LocalEventsMapTheme.dimens.paddingExtraLarge)
            )
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

    val mapConfig = MapConfig(
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
                onNameChange = { onUiEvent(ScreenUiEvent.OnNameChanged(it)) },
                onResetFilters = { onUiEvent(ScreenUiEvent.OnResetFilters) }
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
        mapMarkers = uiState.mapMarkers,
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
    val layoutDirection = LocalLayoutDirection.current
    val systemInsets = WindowInsets.systemGestures
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    consumeGesturesForSystemInsets(systemInsets, layoutDirection)
                }
            }
    ) {
        LocalEventsMapComponent(
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
            mapScreenActions = mapScreenActions,
            modifier = Modifier.fillMaxWidth().statusBarsPadding()
        )
        FunctionalBlock(
            mapScreenData = MapScreenData(
                name = uiState.filterState.name,
                selectedEvent = selectedEvent,
                isSyncLoading = uiState.isSyncLoading ?: false,
                mapScreenActions = mapScreenActions,
                onMapEventsListExpanded = { state.mapEventsListExpanded = !state.mapEventsListExpanded },
                onFiltersExpanded = { state.filtersExpanded = !state.filtersExpanded },
                isEventSelected = selectedEvent != null,
                onMapEventSelectedItemClick = { mapScreenActions.onNavigateToDetailsScreen(it) }
            )
        )
    }
}

private suspend fun AwaitPointerEventScope.consumeGesturesForSystemInsets(
    systemInsets: WindowInsets,
    layoutDirection: LayoutDirection
) {
    while (true) {
        val event = awaitPointerEvent(PointerEventPass.Initial)
        val change = event.changes.first()
        val pos = change.position

        val leftZone = systemInsets.getLeft(this, layoutDirection).toFloat()
        val rightZone = size.width - systemInsets.getRight(this, layoutDirection).toFloat()
        val bottomZone = size.height - systemInsets.getBottom(this).toFloat()

        val isInEdge = pos.x <= leftZone || pos.x >= rightZone || pos.y >= bottomZone

        if (isInEdge) {
            change.consume()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoxScope.MapBottomSheets(
    uiState: ScreenState.Success,
    state: MapScreenLocalState,
    mapScreenActions: MapScreenActions,
    modifier: Modifier = Modifier
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
        eventsList = uiState.eventsList,
        modifier = modifier
    )

    FilterBottomSheetContent(
        data = BottomSheetData(
            expanded = state.filtersExpanded,
            onDismissSheet = { state.filtersExpanded = !state.filtersExpanded },
            sheetState = state.filtersSheetState
        ),
        filtersState = uiState.filterState,
        mapScreenActions = mapScreenActions,
        modifier = Modifier.fillMaxWidth().systemBarsPadding()
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
                citiesList = CityCoordinates.entries.toImmutableList(),
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
                    .background(color = Color.Transparent)
            )
        }
        uiState.dataStatus?.let { dataStatus ->
            StatusBanner(
                status = dataStatus,
                modifier = Modifier.fillMaxWidth()
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
