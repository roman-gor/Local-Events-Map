package com.gorman.data.repository

import com.gorman.common.constants.CityCoordinatesConstants
import com.gorman.common.data.LocationProvider
import com.gorman.common.models.CityData
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.Address
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.ToponymObjectMetadata
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class GeoRepositoryImpl @Inject constructor(
    private val locationProvider: LocationProvider
) : IGeoRepository {
    private val searchManager = SearchFactory.getInstance()
        .createSearchManager(SearchManagerType.COMBINED)

    override suspend fun getCityByPoint(location: Point): CityData? = suspendCancellableCoroutine { continuation ->
        val searchOptions = SearchOptions().apply {
            searchTypes = SearchType.GEO.value
            resultPageSize = 1
        }
        val searchSession = searchManager.submit(
            location,
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

                    val cityEnum = cityName?.let { CityCoordinatesConstants.fromCityName(it) }

                    if (cityEnum != null) {
                        continuation.resume(CityData(city = cityEnum, cityCoordinates = location))
                    } else {
                        continuation.resume(null)
                    }
                }

                override fun onSearchError(p0: com.yandex.runtime.Error) {
                    continuation.resume(null)
                }
            }
        )
        continuation.invokeOnCancellation {
            searchSession.cancel()
        }
    }

    override suspend fun getPointByCity(
        city: CityCoordinatesConstants
    ): CityData? = suspendCancellableCoroutine { continuation ->
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
                        continuation.resume(CityData(city = city, cityCoordinates = point))
                    } else {
                        continuation.resume(null)
                    }
                }

                override fun onSearchError(p0: com.yandex.runtime.Error) {
                    continuation.resume(null)
                }
            }
        )
        continuation.invokeOnCancellation {
            searchSession.cancel()
        }
    }

    override suspend fun getUserLocation(): Point? =
        locationProvider.getLastKnownLocation()
}
