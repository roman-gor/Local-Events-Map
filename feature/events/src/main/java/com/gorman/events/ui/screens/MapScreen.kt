package com.gorman.events.ui.screens

import android.content.Context
import android.graphics.PointF
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gorman.domain_model.MapEvent
import com.gorman.events.R
import com.gorman.events.ui.components.BottomEventsListSheetDialog
import com.gorman.events.ui.components.BottomFiltersSheetDialog
import com.gorman.events.ui.components.LoadingStub
import com.gorman.events.ui.constants.categoriesList
import com.gorman.events.ui.states.FiltersState
import com.gorman.events.ui.states.MapEventsState
import com.gorman.events.ui.viewmodels.MapViewModel
import com.gorman.ui.theme.LocalEventsMapTheme
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.launch

@Composable
fun MapScreenEntry(mapViewModel: MapViewModel = hiltViewModel()) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        mapViewModel.syncEvents()
        mapViewModel.getEventsList()
    }
    val mapEventsState by mapViewModel.mapEventState.collectAsStateWithLifecycle()
    when (val state = mapEventsState) {
        is MapEventsState.Error -> ErrorDataScreen()
        MapEventsState.Idle -> Box(Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background))
        MapEventsState.Loading -> LoadingStub()
        is MapEventsState.Success -> {
            val selectedEvent by mapViewModel.selectedEventId.collectAsStateWithLifecycle()
            val filters by mapViewModel.filterState.collectAsStateWithLifecycle()
            val coordinatesList = state.eventsList.map { event ->
                val (lat, lon) = event.coordinates.split(",").map { it.trim().toDouble() }
                Pair(lat, lon)
            }
            MapScreen(
                context = context,
                selectedMapEvent = selectedEvent,
                filters = filters,
                onCategoryChange = { mapViewModel.onCategoryChanged(it) },
                onEventClick = { mapViewModel.selectEvent(it.localId) },
                eventsList = state.eventsList,
                coordinatesList = coordinatesList
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    context: Context,
    selectedMapEvent: MapEvent?,
    filters: FiltersState,
    onCategoryChange: (String) -> Unit,
    onEventClick: (MapEvent) -> Unit,
    eventsList: List<MapEvent>,
    coordinatesList: List<Pair<Double, Double>>
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
            context = context,
            selectedMapEvent = selectedMapEvent,
            onEventClick = onEventClick,
            eventsList = eventsList,
            coordinates = coordinatesList
        )
        if (mapEventsListExpanded)
            BottomEventsListSheetDialog(
                onDismiss = { mapEventsListExpanded = !mapEventsListExpanded },
                selectedMapEvent = selectedMapEvent,
                onEventClick = {
                    onEventClick(it)
                    scope.launch {
                        mapEventsListSheetState.hide()
                        mapEventsListExpanded = false
                    } },
                eventsList = eventsList,
                sheetState = mapEventsListSheetState
            )
        if (filtersExpanded)
            BottomFiltersSheetDialog(
                onDismiss = { filtersExpanded = !filtersExpanded },
                sheetState = filtersSheetState,
                categoryItems = categoriesList,
                filters = filters,
                costItems = listOf("Платно", "Бесплатно"),
                onCategoryChange = onCategoryChange,
                onDateRangeChange = { },
                onDistanceChange = {},
                onCostChange = {},
                onNameChange = { }
            )
        Button(
            onClick = { mapEventsListExpanded = !mapEventsListExpanded },
            shape = CircleShape,
            modifier = Modifier
                .padding(LocalEventsMapTheme.dimens.paddingExtraLarge)
                .size(48.dp)
                .align(alignment = Alignment.BottomStart)
                .offset(y = verticalOffset),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 12.dp
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Menu,
                contentDescription = "list_button",
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.secondary)
        }
        Button(
            onClick = { filtersExpanded = !filtersExpanded },
            shape = CircleShape,
            modifier = Modifier
                .padding(LocalEventsMapTheme.dimens.paddingExtraLarge)
                .size(48.dp)
                .align(alignment = Alignment.BottomEnd),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 12.dp
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "list_button",
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.secondary)
        }
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
                    if (mapEvent.localId == selectedMapEvent?.localId) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.background
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
    context: Context,
    onEventClick: (MapEvent) -> Unit,
    selectedMapEvent: MapEvent?,
    eventsList: List<MapEvent>,
    coordinates: List<Pair<Double, Double>>
){
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
    DisposableEffect(Unit) {
        mapView.onStart()
        MapKitFactory.getInstance().onStart()

        onDispose {
            mapView.onStop()
            MapKitFactory.getInstance().onStop()
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
    val cameraPosition = Point(53.886944, 27.566667)
    val normalIcon = remember { ImageProvider.fromResource(context, R.drawable.ic_marker) }
    val selectedIcon = remember { ImageProvider.fromResource(context, R.drawable.ic_marker_selected) }
    val points = mutableListOf<Point>()
    coordinates.forEach { coordinate ->
        points.add(Point(coordinate.first, coordinate.second))
    }
    mapView.mapWindow.map.move(
        CameraPosition(
            cameraPosition,
            10.5f,
            0.0f,
            0.0f
        )
    )
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
                mapView.mapWindow.map.mapObjects.addPlacemark().apply{
                    geometry = Point(coordinates[index].first, coordinates[index].second)
                    setIcon(if (isSelected) selectedIcon else normalIcon, iconStyle)
                    userData = event
                    addTapListener(tapListener)
                }
            }
        }
    )
}
