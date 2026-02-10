package com.gorman.map.search

import com.gorman.common.constants.CityCoordinates
import com.gorman.common.models.CityData
import com.gorman.domainmodel.PointDomain
import com.gorman.map.mapper.toYandex
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geo
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.Address
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.ToponymObjectMetadata
import com.yandex.runtime.Error
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class MapSearchManager @Inject constructor(
    private val searchManager: SearchManager
) : IMapSearchManager {
    override fun calculatingDistance(point1: PointDomain, point2: PointDomain): Int =
        Geo.distance(point1.toYandex(), point2.toYandex()).toInt() / 1000

    override suspend fun getCityByPoint(point: PointDomain) = suspendCancellableCoroutine { continuation ->
        val searchOptions = SearchOptions().apply {
            searchTypes = SearchType.GEO.value
            resultPageSize = 1
        }
        val searchSession = searchManager.submit(
            point.toYandex(),
            null,
            searchOptions,
            object : Session.SearchListener {
                override fun onSearchResponse(p0: Response) {
                    val geoObject = p0.collection.children.firstOrNull()?.obj
                        ?.metadataContainer
                        ?.getItem(ToponymObjectMetadata::class.java)

                    val cityName = geoObject?.address?.components
                        ?.firstOrNull { it.kinds.contains(Address.Component.Kind.LOCALITY) }?.name
                        ?: geoObject?.address?.components
                            ?.firstOrNull { it.kinds.contains(Address.Component.Kind.AREA) }?.name

                    val cityEnum = cityName?.let { CityCoordinates.fromCityName(it) }

                    if (cityEnum != null) {
                        continuation.resume(
                            CityData(
                                city = cityEnum,
                                latitude = point.latitude,
                                longitude = point.longitude
                            )
                        )
                    } else {
                        continuation.resume(null)
                    }
                }

                override fun onSearchError(p0: Error) {
                    continuation.resume(null)
                }
            }
        )
        continuation.invokeOnCancellation {
            searchSession.cancel()
        }
    }

    override suspend fun getPointByCity(city: CityCoordinates) = suspendCancellableCoroutine { continuation ->
        val searchOptions = SearchOptions().apply {
            searchTypes = SearchType.GEO.value
            resultPageSize = 1
        }
        val boundingBox = BoundingBox(
            Point(49.0, 22.0),
            Point(57.0, 33.0)
        )
        val searchSession = searchManager.submit(
            city.cityName,
            Geometry.fromBoundingBox(boundingBox),
            searchOptions,
            object : Session.SearchListener {
                override fun onSearchResponse(response: Response) {
                    val firstGeoObject = response.collection.children.firstOrNull()?.obj
                    val point = firstGeoObject?.geometry?.first()?.point
                    if (point != null) {
                        continuation.resume(
                            CityData(
                                city = city,
                                latitude = point.latitude,
                                longitude = point.longitude
                            )
                        )
                    } else {
                        continuation.resume(null)
                    }
                }

                override fun onSearchError(p0: Error) {
                    continuation.resume(null)
                }
            }
        )
        continuation.invokeOnCancellation {
            searchSession.cancel()
        }
    }
}
