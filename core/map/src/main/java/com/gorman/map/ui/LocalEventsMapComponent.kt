package com.gorman.map.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LifecycleStartEffect
import kotlinx.collections.immutable.ImmutableList

@Composable
fun LocalEventsMapComponent(
    markers: ImmutableList<MapMarker>,
    mapControl: MapControl,
    config: MapConfig,
    onCameraIdle: (Latitude, Longitude, Zoom) -> Unit,
    onMapClick: () -> Unit,
    modifier: Modifier = Modifier,
    onMarkerClick: (String) -> Unit
) {
    val context = LocalContext.current
    val internalControl = mapControl as? MapControlImpl

    val isDarkTheme = isSystemInDarkTheme()

    val localEventsMap = remember {
        LocalEventsMap(context, config)
    }
    localEventsMap.onMarkerClick = onMarkerClick
    localEventsMap.onMapClick = onMapClick
    localEventsMap.onCameraIdle = onCameraIdle

    LifecycleStartEffect(Unit) {
        localEventsMap.onStart()
        onStopOrDispose {
            localEventsMap.onStop()
        }
    }

    DisposableEffect(localEventsMap.mapView) {
        internalControl?.mapView = localEventsMap.mapView
        localEventsMap.addListeners()
        onDispose {
            localEventsMap.removeListeners()
            internalControl?.mapView = null
        }
    }

    AndroidView(
        factory = { localEventsMap.mapView },
        modifier = modifier,
        update = {
            localEventsMap.isDarkMode = isDarkTheme
            localEventsMap.checkCurrentMarkers(markers)
            localEventsMap.updateUserLocationOnly(config)
        }
    )
}
