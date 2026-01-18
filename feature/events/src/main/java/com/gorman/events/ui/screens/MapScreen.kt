package com.gorman.events.ui.screens

import android.Manifest
import android.graphics.PointF
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.gorman.common.constants.CategoryConstants.Companion.categoriesList
import com.gorman.common.constants.CostConstants
import com.gorman.events.R
import com.gorman.events.ui.components.cityNameDefinition
import com.gorman.events.ui.components.FiltersBottomSheet
import com.gorman.events.ui.components.FunctionalButton
import com.gorman.events.ui.components.LoadingStub
import com.gorman.events.ui.components.MapEventsBottomSheet
import com.gorman.events.ui.states.FilterActions
import com.gorman.events.ui.states.FilterOptions
import com.gorman.events.ui.states.MapEventsState
import com.gorman.events.ui.states.MapUiEvent
import com.gorman.events.ui.states.MapUiState
import com.gorman.events.ui.viewmodels.MapViewModel
import com.gorman.ui.theme.LocalEventsMapTheme
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
import kotlinx.coroutines.launch
import kotlin.collections.map

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreenEntry(
    mapViewModel: MapViewModel = hiltViewModel()
) {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    val dataLoaded = rememberSaveable { mutableStateOf(false) }
    val cityData by mapViewModel.cityCenterData.collectAsStateWithLifecycle()

    when {
        permissionsState.allPermissionsGranted -> {
            if (!dataLoaded.value) {
                LaunchedEffect(Unit) {
                    mapViewModel.getEventsList()
                    mapViewModel.fetchInitialLocation()
                    dataLoaded.value = true
                }
            }
            MapContent(mapViewModel)
        }
        permissionsState.shouldShowRationale -> {
            PermissionRequestScreen(
                showManualInput = false,
                onCitySubmit = { },
                shouldShowRationale = true,
                requestPermissions = { permissionsState.launchMultiplePermissionRequest() }
            )
        }
        else -> {
            if (cityData.cityCoordinates == null) {
                PermissionRequestScreen(
                    showManualInput = !permissionsState.shouldShowRationale && dataLoaded.value,
                    onCitySubmit = { city -> mapViewModel.searchForCity(city) },
                    shouldShowRationale = false,
                    requestPermissions = { permissionsState.launchMultiplePermissionRequest() }
                )
                LaunchedEffect(Unit) {
                    if (!dataLoaded.value) {
                        permissionsState.launchMultiplePermissionRequest()
                        dataLoaded.value = true
                    }
                }
            } else {
                if (!dataLoaded.value) {
                    LaunchedEffect(Unit) {
                        mapViewModel.getEventsList()
                        dataLoaded.value = true
                    }
                }
                MapContent(mapViewModel)
            }
        }
    }
}

@Composable
fun MapContent(mapViewModel: MapViewModel) {
    val mapEventsState by mapViewModel.mapEventState.collectAsStateWithLifecycle()
    when (val state = mapEventsState) {
        is MapEventsState.Error -> ErrorDataScreen()
        MapEventsState.Idle -> Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )

        MapEventsState.Loading -> LoadingStub()
        is MapEventsState.Success -> {
            val filters by mapViewModel.filterState.collectAsStateWithLifecycle()
            val cityData by mapViewModel.cityCenterData.collectAsStateWithLifecycle()
            val cityChanged by mapViewModel.cityChanged.collectAsStateWithLifecycle(initialValue = true)
            MapScreen(
                onCameraIdle = { location -> location?.let { mapViewModel.onCameraIdle(it) } },
                onCategoryChange = { mapViewModel.onCategoryChanged(it) },
                onSyncClick = { mapViewModel.syncEvents() },
                onEventClick = { mapViewModel.selectEvent(it.id) },
                mapUiState = MapUiState(
                    filters = filters,
                    eventsList = state.eventsList,
                    cityData = cityData,
                    cityChanged = cityChanged
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onCameraIdle: (Point?) -> Unit,
    onCategoryChange: (String) -> Unit,
    onSyncClick: () -> Unit,
    onEventClick: (MapUiEvent) -> Unit,
    mapUiState: MapUiState
) {
    var mapEventsListExpanded by remember { mutableStateOf(false) }
    var filtersExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val mapEventsListSheetState = rememberModalBottomSheetState()
    val filtersSheetState = rememberModalBottomSheetState()
    val filtersButtonVerticalOffset by animateDpAsState(
        targetValue = if (filtersExpanded) (-600).dp else 0.dp,
        animationSpec = tween(durationMillis = 500),
        label = "verticalOffsetAnimation"
    )
    val listEventsButtonVerticalOffset by animateDpAsState(
        targetValue = if (mapEventsListExpanded) (-600).dp else 0.dp,
        animationSpec = tween(durationMillis = 500),
        label = "verticalOffsetAnimation"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        YandexMapView(
            onCameraIdle = onCameraIdle,
            onMarkerClick = { eventId ->
                mapUiState.eventsList.find { it.id == eventId }?.let {
                    onEventClick(it)
                }
            },
            eventsList = mapUiState.eventsList,
            cityCenter = mapUiState.cityData.cityCoordinates,
            cityChanged = mapUiState.cityChanged
        )
        mapUiState.cityData.city?.let {
            Text(
                text = cityNameDefinition(it),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.systemBarsPadding().align(Alignment.TopCenter)
            )
        }
        if (mapEventsListExpanded) {
            MapEventsBottomSheet(
                onDismiss = { mapEventsListExpanded = !mapEventsListExpanded },
                onEventClick = {
                    onEventClick(it)
                    scope.launch {
                        mapEventsListSheetState.hide()
                        mapEventsListExpanded = false
                    }
                },
                eventsList = mapUiState.eventsList,
                sheetState = mapEventsListSheetState
            )
        }
        if (filtersExpanded) {
            FiltersBottomSheet(
                onDismiss = { filtersExpanded = !filtersExpanded },
                sheetState = filtersSheetState,
                filters = mapUiState.filters,
                options = FilterOptions(
                    categoryItems = categoriesList,
                    costItems = CostConstants.costList.map { it.value }
                ),
                actions = FilterActions(
                    onCategoryChange = onCategoryChange,
                    onDateRangeChange = { },
                    onDistanceChange = { },
                    onCostChange = { },
                    onNameChange = { }
                )
            )
        }
        FunctionalButton(
            onClick = { onSyncClick() },
            iconSize = 32.dp,
            imageVector = Icons.Outlined.Refresh,
            modifier = Modifier
                .padding(LocalEventsMapTheme.dimens.paddingExtraLarge)
                .size(48.dp)
                .align(alignment = Alignment.CenterEnd)
        )
        FunctionalButton(
            onClick = { mapEventsListExpanded = !mapEventsListExpanded },
            iconSize = 32.dp,
            imageVector = Icons.Outlined.Menu,
            modifier = Modifier
                .padding(LocalEventsMapTheme.dimens.paddingExtraLarge)
                .size(48.dp)
                .align(alignment = Alignment.BottomStart)
                .offset(y = listEventsButtonVerticalOffset)
        )
        FunctionalButton(
            onClick = { filtersExpanded = !filtersExpanded },
            iconSize = 32.dp,
            painter = painterResource(R.drawable.filter_alt),
            modifier = Modifier
                .padding(LocalEventsMapTheme.dimens.paddingExtraLarge)
                .size(48.dp)
                .align(alignment = Alignment.BottomEnd)
                .offset(y = filtersButtonVerticalOffset)
        )
    }
}

@Composable
fun EventItem(
    mapEvent: MapUiEvent,
    onEventClick: (MapUiEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color =
                    if (mapEvent.isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.background
                    }
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        mapEvent.name?.let {
            Text(
                text = it,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(vertical = LocalEventsMapTheme.dimens.paddingLarge)
                    .clickable(onClick = { onEventClick(mapEvent) })
            )
        }
    }
}

@Composable
fun YandexMapView(
    cityChanged: Boolean,
    onCameraIdle: (Point?) -> Unit,
    onMarkerClick: (String) -> Unit,
    eventsList: ImmutableList<MapUiEvent>,
    cityCenter: Point?
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    val normalIcon = remember { ImageProvider.fromResource(context, R.drawable.ic_marker) }
    val selectedIcon = remember {
        ImageProvider.fromResource(context, R.drawable.ic_marker_selected)
    }

    val tapListener = remember(onMarkerClick) {
        MapObjectTapListener { mapObject, _ ->
            val eventId = mapObject.userData as? String
            Log.d("MapDebug", "Marker clicked: $eventId")
            if (eventId != null) {
                onMarkerClick(eventId)
            }
            true
        }
    }

    DisposableEffect(Unit) {
        mapView.onStart()
        onDispose {
            mapView.onStop()
            MapKitFactory.getInstance().onStop()
        }
    }
    DisposableEffect(mapView) {
        val cameraListener = CameraListener { _, _, reason, finished ->
            if (finished && reason == CameraUpdateReason.GESTURES) {
                onCameraIdle(mapView.mapWindow.map.cameraPosition.target)
            }
        }
        mapView.mapWindow.map.addCameraListener(cameraListener)
        onDispose {
            mapView.mapWindow.map.removeCameraListener(cameraListener)
        }
    }

    val cameraPosition = cityCenter ?: Point(53.886944, 27.566667)
    if (cityChanged) {
        mapView.mapWindow.map.move(
            CameraPosition(
                cameraPosition,
                10.5f,
                0.0f,
                0.0f
            )
        )
    }
    val iconStyle = IconStyle().apply {
        anchor = PointF(0.5f, 1.0f)
        zIndex = 10f
    }
    AndroidView(
        factory = { mapView },
        modifier = Modifier
            .fillMaxSize()
            .clip(LocalEventsMapTheme.shapes.medium),
        update = {
            val mapObjects = mapView.mapWindow.map.mapObjects
            mapObjects.clear()
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
                val coordinates = selectedEvent.coordinates?.split(",")
                    ?.mapNotNull { it.trim().toDoubleOrNull() }
                if (coordinates != null && coordinates.size == 2) {
                    val point = Point(coordinates[0], coordinates[1])
                    mapView.mapWindow.map.move(
                        CameraPosition(point, 15.0f, 0.0f, 0.0f),
                        Animation(Animation.Type.SMOOTH, 0.5f),
                        null
                    )
                }
            } else if (cityChanged && cityCenter != null) {
                mapView.mapWindow.map.move(
                    CameraPosition(cityCenter, 11.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 1f),
                    null
                )
            }
        }
    )
}
