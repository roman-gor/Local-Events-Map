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
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.gorman.common.constants.CategoryConstants.Companion.categoriesList
import com.gorman.common.constants.CostConstants
import com.gorman.events.R
import com.gorman.events.ui.components.FiltersBottomSheet
import com.gorman.events.ui.components.FunctionalButton
import com.gorman.events.ui.components.LoadingStub
import com.gorman.events.ui.components.MapEventsBottomSheet
import com.gorman.events.ui.components.cityNameDefinition
import com.gorman.events.ui.states.FilterActions
import com.gorman.events.ui.states.FilterOptions
import com.gorman.events.ui.states.MapSideEffect
import com.gorman.events.ui.states.MapUiEvent
import com.gorman.events.ui.states.ScreenState
import com.gorman.events.ui.states.ScreenUiEvent
import com.gorman.events.ui.utils.MapController
import com.gorman.events.ui.utils.rememberMapController
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

    val mapController = rememberMapController()

    LaunchedEffect(Unit) {
        mapViewModel.sideEffect.collect { effect ->
            when (effect) {
                is MapSideEffect.MoveCamera -> {
                    mapController.moveCamera(effect.point, effect.zoom)
                }
            }
        }
    }

    BindPermissionLogic(
        permissionsState = permissionsState,
        onPermissionsGranted = { mapViewModel.onUiEvent(ScreenUiEvent.PermissionsGranted) }
    )

    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()

    when (permissionsState.allPermissionsGranted) {
        true -> {
            mapViewModel.onUiEvent(ScreenUiEvent.PermissionsGranted)
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
                val cityHasCoordinates = (uiState as? ScreenState.Success)?.cityCenterData?.cityCoordinates != null

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

@Composable
fun MapContent(
    uiState: ScreenState,
    onUiEvent: (ScreenUiEvent) -> Unit,
    mapController: MapController
) {
    when (uiState) {
        is ScreenState.Error -> ErrorDataScreen()
        ScreenState.Loading -> LoadingStub()
        is ScreenState.Success -> {
            MapScreen(
                onCameraIdle = { location -> location?.let { onUiEvent(ScreenUiEvent.OnCameraIdle(location)) } },
                onCategoryChange = { category -> onUiEvent(ScreenUiEvent.OnCategoryChanged(category)) },
                onSyncClick = { onUiEvent(ScreenUiEvent.OnSyncClicked) },
                onEventClick = { event -> onUiEvent(ScreenUiEvent.OnEventSelected(event.id)) },
                uiState = uiState,
                mapController = mapController
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
    uiState: ScreenState.Success,
    mapController: MapController
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
            mapController = mapController,
            onCameraIdle = onCameraIdle,
            onMarkerClick = { eventId ->
                uiState.eventsList.find { it.id == eventId }?.let {
                    onEventClick(it)
                }
            },
            eventsList = uiState.eventsList
        )
        uiState.cityCenterData.city?.let {
            Text(
                text = cityNameDefinition(it),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .systemBarsPadding()
                    .align(Alignment.TopCenter)
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
                eventsList = uiState.eventsList,
                sheetState = mapEventsListSheetState
            )
        }
        if (filtersExpanded) {
            FiltersBottomSheet(
                onDismiss = { filtersExpanded = !filtersExpanded },
                sheetState = filtersSheetState,
                filters = uiState.filterState,
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
    mapController: MapController,
    onCameraIdle: (Point?) -> Unit,
    onMarkerClick: (String) -> Unit,
    eventsList: ImmutableList<MapUiEvent>
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
            if (eventId != null) onMarkerClick(eventId)
            true
        }
    }

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
                onCameraIdle(mapView.mapWindow.map.cameraPosition.target)
            }
        }
        mapView.mapWindow.map.addCameraListener(cameraListener)
        onDispose {
            mapView.mapWindow.map.removeCameraListener(cameraListener)
        }
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
            }
        }
    )
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
