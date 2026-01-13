package com.gorman.events.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gorman.events.R
import com.gorman.events.ui.viewmodels.MapViewModel
import com.gorman.ui.theme.LocalEventsMapTheme
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

@Composable
fun MapScreenEntry(mapViewModel: MapViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        mapViewModel.syncEvents()
        mapViewModel.getEventsList()
    }
    val eventsList by mapViewModel.eventsListState.collectAsStateWithLifecycle()
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
    MapScreen()
}

@Composable
fun MapScreen() {
    YandexMapView()
}

@Composable
fun YandexMapView(){
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    DisposableEffect(Unit) {
        mapView.onStart()
        MapKitFactory.getInstance().onStart()

        onDispose {
            mapView.onStop()
            MapKitFactory.getInstance().onStop()
        }
    }
    val locationPoint = Point(53.908775, 27.586246)
    mapView.mapWindow.map.move(
        CameraPosition(
            locationPoint,
            15.0f,
            0.0f,
            0.0f
        )
    )
    val imageProvider = ImageProvider.fromResource(context, R.drawable.ic_marker)
    mapView.mapWindow.map.mapObjects.addPlacemark().apply{
        geometry = locationPoint
        setIcon(imageProvider)
    }
    AndroidView(
        factory = { mapView },
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = LocalEventsMapTheme.dimens.paddingMedium, vertical = LocalEventsMapTheme.dimens.paddingLarge)
            .height(200.dp)
            .clip(RoundedCornerShape(LocalEventsMapTheme.dimens.cornerRadius))
    )
}
