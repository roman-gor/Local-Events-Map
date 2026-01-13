package com.gorman.events.ui.screens

import android.content.Context
import android.graphics.PointF
import android.util.Log
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gorman.domain_model.Event
import com.gorman.events.R
import com.gorman.events.ui.viewmodels.MapViewModel
import com.gorman.ui.theme.LocalEventsMapTheme
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
    val eventsList by mapViewModel.eventsListState.collectAsStateWithLifecycle()
    val selectedEvent by mapViewModel.selectedEventId.collectAsStateWithLifecycle()
    LaunchedEffect(eventsList) {
        Log.d("ListEvents", "Обновленный список: ${eventsList.size} элементов")
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val isVisible = remember { mutableStateOf(true) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                isVisible.value = false
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    val coordinatesList = eventsList.map { event ->
        val (lat, lon) = event.coordinates.split(",").map { it.trim().toDouble() }
        Pair(lat, lon)
    }

    MapScreen(
        context = context,
        selectedEvent = selectedEvent,
        onEventClick = { mapViewModel.selectEvent(it.localId) },
        eventsList = eventsList,
        coordinatesList = coordinatesList
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    context: Context,
    selectedEvent: Event?,
    onEventClick: (Event) -> Unit,
    eventsList: List<Event>,
    coordinatesList: List<Pair<Double, Double>>
) {
    var expandedMenu by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val verticalOffset by animateDpAsState(
        targetValue = if (expandedMenu) (-600).dp else 0.dp,
        animationSpec = tween(durationMillis = 500),
        label = "verticalOffsetAnimation"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
    ) {
        YandexMapView(
            context = context,
            selectedEvent = selectedEvent,
            onMarkerTap = {},
            eventsList = eventsList,
            coordinates = coordinatesList
        )
        if (expandedMenu)
            BottomSheetDialog(
                onDismiss = { expandedMenu = !expandedMenu },
                onEventClick = {
                    onEventClick(it)
                    scope.launch {
                        sheetState.hide()
                        expandedMenu = false
                    } },
                eventsList = eventsList,
                sheetState = sheetState
            )
        Button(
            onClick = { expandedMenu = !expandedMenu },
            shape = CircleShape,
            modifier = Modifier
                .padding(LocalEventsMapTheme.dimens.paddingExtraLarge)
                .size(48.dp)
                .align(alignment = Alignment.BottomStart)
                .offset(y = verticalOffset),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
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
                tint = Color.Black)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetDialog(
    onDismiss: () -> Unit,
    onEventClick: (Event) -> Unit,
    eventsList: List<Event>,
    sheetState: SheetState
) {
    val configuration = LocalConfiguration.current
    val maxHeight = configuration.screenHeightDp.dp * 0.7f
    ModalBottomSheet(
        onDismissRequest = {onDismiss()},
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.onSecondary,
        shape = RoundedCornerShape(topStart = LocalEventsMapTheme.dimens.cornerRadius,
            topEnd = LocalEventsMapTheme.dimens.cornerRadius),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(maxHeight)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            items(eventsList) { event ->
                EventItem(
                    event,
                    onEventClick = onEventClick
                )
            }
        }
    }
}

@Composable
fun EventItem(
    event: Event,
    onEventClick: (Event) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(LocalEventsMapTheme.dimens.paddingMedium),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        event.name?.let {
            Text(
                text = it,
                fontSize = 14.sp,
                modifier = Modifier.clickable(onClick = { onEventClick(event) })
            )
        }
    }
}

@Composable
fun YandexMapView(
    context: Context,
    onMarkerTap: () -> Unit,
    selectedEvent: Event?,
    eventsList: List<Event>,
    coordinates: List<Pair<Double, Double>>
){
    val mapView = remember { MapView(context) }
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
            val event = mapObject.userData as? Event
            if (event != null) {
                onMarkerTap()
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
                val isSelected = event == selectedEvent
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
