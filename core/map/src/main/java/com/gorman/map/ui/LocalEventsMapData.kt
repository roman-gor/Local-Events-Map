package com.gorman.map.ui

import android.content.Context
import android.graphics.PointF
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.TextView
import androidx.compose.runtime.mutableStateOf
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
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.ui_view.ViewProvider
import kotlinx.collections.immutable.ImmutableList

internal typealias Latitude = Double
internal typealias Longitude = Double
internal typealias Zoom = Float

internal class LocalEventsMapData(
    private val context: Context,
    private val config: MapConfig
) {
    val mapView = MapView(context)
    var isDarkMode = false
        set(value) {
            mapView.mapWindow.map.isNightModeEnabled = value
            field = value
        }
    var onMarkerClick: (String) -> Unit = {}
    var onMapClick: () -> Unit = {}
    var onCameraIdle: (Latitude, Longitude, Zoom) -> Unit = { _, _, _ -> }
    private val cameraListener = CameraListener { _, _, reason, finished ->
        if (finished && reason == CameraUpdateReason.GESTURES) {
            val position = mapView.mapWindow.map.cameraPosition
            onCameraIdle(position.target.latitude, position.target.longitude, position.zoom)
        }
    }
    private val inputListener = object : InputListener {
        override fun onMapTap(p0: Map, p1: Point) {
            onMapClick()
        }

        override fun onMapLongTap(p0: Map, p1: Point) { return }
    }

    private val clusterListener = createClusterListener()
    private val eventsCollection = mutableStateOf<ClusterizedPlacemarkCollection?>(null)
    private val userPlacemark = mutableStateOf<PlacemarkMapObject?>(null)
    private var mapInitialized = false
    val currentMarkers = mutableStateOf<List<MapMarker>?>(null)
    private val markerTapListener = MapObjectTapListener { mapObject, _ ->
        (mapObject.userData as? String)?.let { onMarkerClick(it) }
        true
    }

    fun onStart() {
        mapView.onStart()
        moveCameraTo(
            initialPosition = config.initialPosition,
            userLocation = config.userLocation,
            initialZoom = config.initialZoom ?: 11f
        )
    }

    fun onStop() {
        mapView.onStop()
    }

    fun addListeners() {
        mapView.mapWindow.map.addInputListener(inputListener)
        mapView.mapWindow.map.addCameraListener(cameraListener)
    }

    fun removeListeners() {
        mapView.mapWindow.map.removeInputListener(inputListener)
        mapView.mapWindow.map.removeCameraListener(cameraListener)
    }

    private fun moveCameraTo(
        initialPosition: PointDomain?,
        userLocation: PointDomain?,
        initialZoom: Float = 11f
    ) {
        if (mapInitialized) return

        if (initialPosition != null) {
            mapView.mapWindow.map.move(
                CameraPosition(initialPosition.toYandex(), initialZoom, 0.0f, 0.0f)
            )
        } else if (userLocation != null) {
            mapView.mapWindow.map.move(
                CameraPosition(userLocation.toYandex(), 11.0f, 0.0f, 0.0f)
            )
        }

        mapInitialized = true
    }

    fun createClusterListener(): ClusterListener {
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

    fun updateUserLocationOnly(config: MapConfig) {
        val location = config.userLocation ?: return
        val iconRes = config.userLocationIconRes ?: return

        if (userPlacemark.value == null) {
            val userIcon = ImageProvider.fromResource(context, iconRes)
            userPlacemark.value = mapView.mapWindow.map.mapObjects.addPlacemark().apply {
                geometry = Point(location.latitude, location.longitude)
                setIcon(userIcon)
                zIndex = 0f
                isDraggable = false
            }
        } else {
            userPlacemark.value?.geometry = Point(location.latitude, location.longitude)
        }
    }

    fun checkCurrentMarkers(markers: ImmutableList<MapMarker>) {
        if (currentMarkers.value != markers) {
            updateMarkersOnly(markers)
            currentMarkers.value = markers
        }
    }

    private fun updateMarkersOnly(markers: List<MapMarker>) {
        val collection = eventsCollection.value ?: mapView.mapWindow.map.mapObjects.addClusterizedPlacemarkCollection(
            clusterListener
        ).also { eventsCollection.value = it }

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
                addTapListener(markerTapListener)
            }
        }

        collection.clusterPlacemarks(60.0, 15)
    }
}
