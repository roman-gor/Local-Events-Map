package com.gorman.map.ui

import android.content.Context
import android.graphics.PointF
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.gorman.domainmodel.PointDomain
import com.gorman.map.R
import com.gorman.map.mapper.toYandex
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.ClusterListener
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectTapListener
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
    modifier: Modifier = Modifier,
    markers: ImmutableList<MapMarker>,
    mapControl: MapControl,
    config: MapConfig,
    onMapReady: () -> Unit = {},
    onCameraIdle: (Double, Double) -> Unit,
    onMarkerClick: (String) -> Unit
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val internalControl = mapControl as? MapControlImpl

    DisposableEffect(mapView) {
        internalControl?.mapView = mapView
        mapView.onStart()
        onMapReady()
        onDispose {
            mapView.onStop()
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
            updateMapState(
                mapView = view,
                context = context,
                markers = markers,
                isDarkMode = config.isDarkMode,
                onMarkerClick = onMarkerClick,
                config = config
            )
        }
    )
}

private fun updateMapState(
    mapView: MapView,
    context: Context,
    markers: List<MapMarker>,
    isDarkMode: Boolean,
    onMarkerClick: (String) -> Unit,
    config: MapConfig
) {
    mapView.mapWindow.map.isNightModeEnabled = isDarkMode
    val mapObjects = mapView.mapWindow.map.mapObjects
    mapObjects.clear()

    if (config.userLocation != null && config.userLocationIconRes != null) {
        val userIcon = ImageProvider.fromResource(context, config.userLocationIconRes)

        mapObjects.addPlacemark().apply {
            geometry = Point(config.userLocation.latitude, config.userLocation.longitude)
            setIcon(userIcon)
            zIndex = 0f
            isDraggable = false
        }
    }

    val tapListener = MapObjectTapListener { mapObject, _ ->
        val id = mapObject.userData as? String
        id?.let { onMarkerClick(it) }
        true
    }

    val clusterListener = ClusterListener { cluster ->
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

    val clusterizedCollection = mapObjects.addClusterizedPlacemarkCollection(clusterListener)

    val iconStyle = IconStyle().apply {
        anchor = PointF(0.5f, 1.0f)
        zIndex = 10f
    }

    markers.forEach { marker ->
        val imageProvider = ImageProvider.fromResource(
            context,
            if (marker.isSelected) marker.selectedIconRes else marker.iconRes
        )

        clusterizedCollection.addPlacemark().apply {
            geometry = Point(marker.latitude, marker.longitude)
            setIcon(imageProvider, iconStyle)
            userData = marker.id
            addTapListener(tapListener)
        }
    }

    clusterizedCollection.clusterPlacemarks(60.0, 15)
}
