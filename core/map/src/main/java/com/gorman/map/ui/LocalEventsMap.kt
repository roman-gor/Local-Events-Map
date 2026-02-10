package com.gorman.map.ui

import android.content.Context
import android.graphics.PointF
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LifecycleStartEffect
import com.gorman.domainmodel.PointDomain
import com.gorman.map.R
import com.gorman.map.mapper.toYandex
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.ClusterListener
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.ui_view.ViewProvider
import kotlinx.collections.immutable.ImmutableList

private class MapControlImpl : MapControl {
    var mapView: MapView? = null

    override fun moveCamera(point: PointDomain, zoom: Float) {
        mapView?.mapWindow?.map?.move(
            CameraPosition(point.toYandex(), zoom, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    }
}

@Composable
fun rememberMapControl(): MapControl {
    return remember { MapControlImpl() }
}

@Composable
fun LocalEventsMap(
    markers: ImmutableList<MapMarker>,
    mapControl: MapControl,
    config: MapConfig,
    onCameraIdle: (Double, Double) -> Unit,
    modifier: Modifier = Modifier,
    onMapReady: () -> Unit = {},
    onMarkerClick: (String) -> Unit
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val internalControl = mapControl as? MapControlImpl

    val eventsCollection = remember { mutableStateOf<ClusterizedPlacemarkCollection?>(null) }
    val userPlacemark = remember { mutableStateOf<PlacemarkMapObject?>(null) }

    val clusterListener = remember(context) { createClusterListener(context, mapView) }

    val latestOnMarkerClick by rememberUpdatedState(onMarkerClick)

    val markerTapListener = remember {
        MapObjectTapListener { mapObject, _ ->
            (mapObject.userData as? String)?.let { latestOnMarkerClick(it) }
            true
        }
    }

    val currentMarkers = remember { mutableStateOf<List<MapMarker>?>(null) }

    LifecycleStartEffect(Unit) {
        mapView.onStart()
        onStopOrDispose {
            mapView.onStop()
        }
    }

    DisposableEffect(mapView) {
        internalControl?.mapView = mapView
        onMapReady()
        onDispose {
            internalControl?.mapView = null
        }
    }

    DisposableEffect(mapView) {
        val cameraListener = CameraListener { _, _, reason, finished ->
            if (finished && reason == CameraUpdateReason.GESTURES) {
                val target = mapView.mapWindow.map.cameraPosition.target
                onCameraIdle(target.latitude, target.longitude)
            }
        }
        mapView.mapWindow.map.addCameraListener(cameraListener)
        onDispose { mapView.mapWindow.map.removeCameraListener(cameraListener) }
    }

    LaunchedEffect(config.userLocation) {
        config.userLocation?.let {
            mapView.mapWindow.map.move(
                CameraPosition(it.toYandex(), 11.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
        update = { view ->
            if (view.mapWindow.map.isNightModeEnabled != config.isDarkMode) {
                view.mapWindow.map.isNightModeEnabled = config.isDarkMode
            }

            if (currentMarkers.value != markers) {
                updateMarkersOnly(
                    mapView = view,
                    context = context,
                    markers = markers,
                    tapListener = markerTapListener,
                    clusterListener = clusterListener,
                    collectionState = eventsCollection
                )
                currentMarkers.value = markers
            }

            updateUserLocationOnly(
                mapView = view,
                context = context,
                config = config,
                userPlacemarkState = userPlacemark
            )
        }
    )
}

private fun updateMarkersOnly(
    mapView: MapView,
    context: Context,
    markers: List<MapMarker>,
    tapListener: MapObjectTapListener,
    clusterListener: ClusterListener,
    collectionState: MutableState<ClusterizedPlacemarkCollection?>
) {
    val collection = collectionState.value ?: mapView.mapWindow.map.mapObjects.addClusterizedPlacemarkCollection(
        clusterListener
    ).also { collectionState.value = it }

    collection.clear()

    val iconStyle = IconStyle().apply {
        anchor = PointF(0.5f, 1.0f)
        zIndex = 10f
    }

    markers.forEach { marker ->
        val imageProvider = ImageProvider.fromResource(
            context,
            if (marker.isSelected) marker.selectedIconRes else marker.iconRes
        )
        collection.addPlacemark().apply {
            geometry = Point(marker.latitude, marker.longitude)
            setIcon(imageProvider, iconStyle)
            userData = marker.id
            addTapListener(tapListener)
        }
    }

    collection.clusterPlacemarks(60.0, 15)
}

private fun updateUserLocationOnly(
    mapView: MapView,
    context: Context,
    config: MapConfig,
    userPlacemarkState: MutableState<PlacemarkMapObject?>
) {
    val location = config.userLocation ?: return
    val iconRes = config.userLocationIconRes ?: return

    if (userPlacemarkState.value == null) {
        val userIcon = ImageProvider.fromResource(context, iconRes)
        userPlacemarkState.value = mapView.mapWindow.map.mapObjects.addPlacemark().apply {
            geometry = Point(location.latitude, location.longitude)
            setIcon(userIcon)
            zIndex = 0f
            isDraggable = false
        }
    } else {
        userPlacemarkState.value?.geometry = Point(location.latitude, location.longitude)
    }
}

private fun createClusterListener(context: Context, mapView: MapView): ClusterListener {
    return ClusterListener { cluster ->
        val textView = TextView(context).apply {
            text = cluster.size.toString()
            textSize = 14f
            setTextColor(context.getColor(R.color.onPrimary))
            gravity = Gravity.CENTER
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(context.getColor(R.color.primary))
                setSize(100, 100)
            }
            setPadding(20, 20, 20, 20)
        }
        cluster.appearance.setView(ViewProvider(textView))
        cluster.appearance.zIndex = 100f
        cluster.addClusterTapListener { _ ->
            val target = cluster.appearance.geometry
            mapView.mapWindow.map.move(
                CameraPosition(target, mapView.mapWindow.map.cameraPosition.zoom + 2, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 0.5f),
                null
            )
            true
        }
    }
}
