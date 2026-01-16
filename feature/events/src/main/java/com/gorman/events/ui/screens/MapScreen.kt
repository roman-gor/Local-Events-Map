package com.gorman.events.ui.screens

import android.Manifest
import android.graphics.PointF
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
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
import com.gorman.domainmodel.MapEvent
import com.gorman.events.R
import com.gorman.events.ui.components.MapEventsBottomSheet
import com.gorman.events.ui.components.FiltersBottomSheet
import com.gorman.events.ui.components.CityNameDefinition
import com.gorman.events.ui.components.FunctionalButton
import com.gorman.events.ui.components.LoadingStub
import com.gorman.events.ui.states.FilterActions
import com.gorman.events.ui.states.FilterOptions
import com.gorman.events.ui.states.MapEventsState
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
import kotlinx.coroutines.launch
import kotlin.collections.map

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreenEntry(mapViewModel: MapViewModel = hiltViewModel()) {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    val dataLoaded = rememberSaveable { mutableStateOf(false) }
    val cityData by mapViewModel.cityCenterData.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        mapViewModel.syncEvents()
    }

    when {
        permissionsState.allPermissionsGranted -> {
            if (!dataLoaded.value) {
                LaunchedEffect(Unit) {
                    mapViewModel.syncEvents()
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
                        mapViewModel.syncEvents()
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
            val selectedEvent by mapViewModel.selectedEventId.collectAsStateWithLifecycle()
            val filters by mapViewModel.filterState.collectAsStateWithLifecycle()
            val cityData by mapViewModel.cityCenterData.collectAsStateWithLifecycle()
            val cityChanged by mapViewModel.cityChanged.collectAsStateWithLifecycle(initialValue = true)
            val coordinatesList = state.eventsList.map { event ->
                val (lat, lon) = event.coordinates.split(",").map { it.trim().toDouble() }
                Pair(lat, lon)
            }
            MapScreen(
                onCameraIdle = { location -> location?.let { mapViewModel.onCameraIdle(it) } },
                onCategoryChange = { mapViewModel.onCategoryChanged(it) },
                onEventClick = { mapViewModel.selectEvent(it.localId) },
                mapUiState = MapUiState(
                    selectedMapEvent = selectedEvent,
                    filters = filters,
                    eventsList = state.eventsList,
                    coordinatesList = coordinatesList,
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
    onEventClick: (MapEvent) -> Unit,
    mapUiState: MapUiState
) {
    var mapEventsListExpanded by remember { mutableStateOf(false) }
    var filtersExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val mapEventsListSheetState = rememberModalBottomSheetState()
    val filtersSheetState = rememberModalBottomSheetState()
    val verticalOffset by animateDpAsState(
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
            selectedMapEvent = mapUiState.selectedMapEvent,
            onEventClick = onEventClick,
            eventsList = mapUiState.eventsList,
            coordinates = mapUiState.coordinatesList,
            cityCenter = mapUiState.cityData.cityCoordinates,
            cityChanged = mapUiState.cityChanged
        )
        mapUiState.cityData.city?.let {
            Text(
                text = CityNameDefinition(it),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = LocalEventsMapTheme.dimens.paddingExtraLarge)
            )
        }
        if (mapEventsListExpanded) {
            MapEventsBottomSheet(
                onDismiss = { mapEventsListExpanded = !mapEventsListExpanded },
                selectedMapEvent = mapUiState.selectedMapEvent,
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
            onClick = { mapEventsListExpanded = !mapEventsListExpanded },
            imageVector = Icons.Outlined.Menu,
            modifier = Modifier
                .padding(LocalEventsMapTheme.dimens.paddingExtraLarge)
                .size(48.dp)
                .align(alignment = Alignment.BottomStart)
                .offset(y = verticalOffset)
        )
        FunctionalButton(
            onClick = { filtersExpanded = !filtersExpanded },
            painter = painterResource(R.drawable.filter_alt),
            modifier = Modifier
                .padding(LocalEventsMapTheme.dimens.paddingExtraLarge)
                .size(48.dp)
                .align(alignment = Alignment.BottomEnd)
        )
    }
}

@Composable
fun EventItem(
    mapEvent: MapEvent,
    selectedMapEvent: MapEvent?,
    onEventClick: (MapEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color =
                if (mapEvent.localId == selectedMapEvent?.localId) {
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
    onEventClick: (MapEvent) -> Unit,
    selectedMapEvent: MapEvent?,
    eventsList: List<MapEvent>,
    coordinates: List<Pair<Double, Double>>,
    cityCenter: Point?
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    LaunchedEffect(selectedMapEvent) {
        selectedMapEvent?.let { event ->
            val (lat, lon) = event.coordinates.split(",").map { it.trim().toDouble() }
            val selectedPoint = Point(lat, lon)
            mapView.mapWindow.map.move(
                CameraPosition(selectedPoint, 15.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 0.5f),
                null
            )
        }
    }
    LaunchedEffect(cityCenter, cityChanged) {
        if (cityCenter != null && cityChanged) {
            mapView.mapWindow.map.move(
                CameraPosition(cityCenter, 11.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
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
    val tapListener = remember {
        MapObjectTapListener { mapObject, _ ->
            val mapEvent = mapObject.userData as? MapEvent
            if (mapEvent != null) {
                onEventClick(mapEvent)
            }
            true
        }
    }
    val cameraPosition = cityCenter ?: Point(53.886944, 27.566667)
    val normalIcon = remember { ImageProvider.fromResource(context, R.drawable.ic_marker) }
    val selectedIcon =
        remember { ImageProvider.fromResource(context, R.drawable.ic_marker_selected) }
    val points = mutableListOf<Point>()
    coordinates.forEach { coordinate ->
        points.add(Point(coordinate.first, coordinate.second))
    }
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
            .padding(
                horizontal = LocalEventsMapTheme.dimens.paddingMedium,
                vertical = LocalEventsMapTheme.dimens.paddingLarge
            )
            .height(200.dp)
            .clip(RoundedCornerShape(LocalEventsMapTheme.dimens.cornerRadius)),
        update = {
            val mapObjects = mapView.mapWindow.map.mapObjects
            mapObjects.clear()
            eventsList.forEachIndexed { index, event ->
                val isSelected = event == selectedMapEvent
                mapView.mapWindow.map.mapObjects.addPlacemark().apply {
                    geometry = Point(coordinates[index].first, coordinates[index].second)
                    setIcon(if (isSelected) selectedIcon else normalIcon, iconStyle)
                    userData = event
                    addTapListener(tapListener)
                }
            }
        }
    )
}
