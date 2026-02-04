package com.gorman.feature.events.impl.ui.screens.mapscreen

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.gorman.common.constants.CityCoordinatesConstants
import com.gorman.common.constants.toDisplayName
import com.gorman.domainmodel.PointDomain
import com.gorman.feature.events.impl.R
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
import com.gorman.ui.components.ErrorDataScreen
import com.gorman.ui.components.LoadingStub
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

    val mapControl = rememberMapControl()

    HandleSideEffects(context, mapViewModel, mapControl)

    BindPermissionLogic(
        permissionsState = permissionsState,
        onPermissionsGranted = { mapViewModel.onUiEvent(ScreenUiEvent.PermissionsGranted) }
    )

    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is ScreenState.Error -> ErrorDataScreen(
            text = stringResource(com.gorman.ui.R.string.errorDataLoading),
            onRetryClick = {}
        )
        ScreenState.Loading -> LoadingStub()
        is ScreenState.Success -> MapSuccessContent(
            state = state,
            permissionsState = permissionsState,
            mapControl = mapControl,
            onUiEvent = mapViewModel::onUiEvent,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun MapSuccessContent(
    state: ScreenState.Success,
    permissionsState: MultiplePermissionsState,
    mapControl: MapControl,
    onUiEvent: (ScreenUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val isMapAllowed = state.isPermissionsRequested == true
    val hasCoordinates = state.cityData.cityCoordinates != null
    val shouldShowRationale = permissionsState.shouldShowRationale

    when {
        isMapAllowed -> {
            MapContent(
                modifier = modifier,
                uiState = state,
                onUiEvent = onUiEvent,
                mapControl = mapControl
            )
        }
        shouldShowRationale -> {
            PermissionRequestScreen(
                showManualInput = false,
                onCitySubmit = { },
                shouldShowRationale = true,
                requestPermissions = { permissionsState.launchMultiplePermissionRequest() }
            )
        }
        !hasCoordinates -> {
            PermissionRequestScreen(
                showManualInput = true,
                onCitySubmit = { city ->
                    onUiEvent(ScreenUiEvent.PermissionsRequested)
                    onUiEvent(ScreenUiEvent.OnCitySearch(city))
                },
                shouldShowRationale = false,
                requestPermissions = { permissionsState.launchMultiplePermissionRequest() }
            )
        }
        else -> {
            MapContent(
                modifier = modifier,
                uiState = state,
                onUiEvent = onUiEvent,
                mapControl = mapControl
            )
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
    LifecycleStartEffect(Unit) {
        onUiEvent(ScreenUiEvent.MapKitOnStart)
        onStopOrDispose {
            onUiEvent(ScreenUiEvent.MapKitOnStop)
        }
    }

    val state = rememberMapScreenLocalState()

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
        userLocationIconRes = R.drawable.ic_location_marker
    )

    MapScreen(
        mapScreenActions = MapScreenActions(
            onCameraIdle = { location ->
                location?.let {
                    onUiEvent(ScreenUiEvent.OnCameraIdle(location))
                }
            },
            filterActions = FilterActions(
                onCategoryChange = { onUiEvent(ScreenUiEvent.OnCategoryChanged(it)) },
                onDateRangeChange = { onUiEvent(ScreenUiEvent.OnDateChanged(it)) },
                onDistanceChange = { onUiEvent(ScreenUiEvent.OnDistanceChanged(it)) },
                onCostChange = { onUiEvent(ScreenUiEvent.OnCostChanged(it)) },
                onNameChange = { onUiEvent(ScreenUiEvent.OnNameChanged(it)) }
            ),
            onSyncClick = { onUiEvent(ScreenUiEvent.OnSyncClicked) },
            onEventClick = { event ->
                onUiEvent(ScreenUiEvent.OnEventSelected(event.id))
            },
            onCitySubmit = { city -> onUiEvent(ScreenUiEvent.OnCitySearch(city)) },
            onNavigateToDetailsScreen = { event ->
                onUiEvent(ScreenUiEvent.OnNavigateToDetailsScreen(event))
            }
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

    LaunchedEffect(selectedEvent) {
        if (selectedEvent != null) {
            val coordinates = selectedEvent.coordinates?.split(",")
            if (coordinates != null && coordinates.size >= 2) {
                mapControl.moveCamera(
                    point = PointDomain(coordinates[0].trim().toDouble(), coordinates[1].trim().toDouble()),
                    zoom = 15f
                )
            }
        }
    }

    Box(modifier = modifier) {
        LocalEventsMap(
            modifier = Modifier.fillMaxSize(),
            markers = mapMarkers,
            mapControl = mapControl,
            config = mapConfig,
            onCameraIdle = { lat, lon -> mapScreenActions.onCameraIdle(PointUiState(lat, lon)) },
            onMarkerClick = { id -> uiState.eventsList.find { it.id == id }?.let { mapScreenActions.onEventClick(it) } }
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
        FunctionalBlock(
            mapScreenData = MapScreenData(
                name = uiState.filterState.name,
                selectedEvent = selectedEvent,
                listEventsButtonVerticalOffset = state.listEventsButtonOffset.value,
                filtersButtonVerticalOffset = state.filtersButtonOffset.value,
                mapScreenActions = mapScreenActions,
                onMapEventsListExpanded = { state.mapEventsListExpanded = !state.mapEventsListExpanded },
                onFiltersExpanded = { state.filtersExpanded = !state.filtersExpanded },
                isEventSelected = selectedEvent != null,
                onMapEventSelectedItemClick = { mapScreenActions.onNavigateToDetailsScreen(it) }
            )
        )
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
