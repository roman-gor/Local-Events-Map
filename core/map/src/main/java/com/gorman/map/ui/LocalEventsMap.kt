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
fun LocalEventsMap(
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

    val localEventsMapData = remember {
        LocalEventsMapData(context, config)
    }
    localEventsMapData.onMarkerClick = onMarkerClick
    localEventsMapData.onMapClick = onMapClick
    localEventsMapData.onCameraIdle = onCameraIdle

    LifecycleStartEffect(Unit) {
        localEventsMapData.onStart()
        onStopOrDispose {
            localEventsMapData.onStop()
        }
    }

    DisposableEffect(localEventsMapData.mapView) {
        internalControl?.mapView = localEventsMapData.mapView
        localEventsMapData.addListeners()
        onDispose {
            localEventsMapData.removeListeners()
            internalControl?.mapView = null
        }
    }

    AndroidView(
        factory = { localEventsMapData.mapView },
        modifier = modifier,
        update = {
            localEventsMapData.isDarkMode = isDarkTheme
            localEventsMapData.checkCurrentMarkers(markers)
            localEventsMapData.updateUserLocationOnly(config)
        }
    )
}
