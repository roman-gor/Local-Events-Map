package com.gorman.data.repository

import com.gorman.cache.data.DataStoreManager
import com.gorman.common.constants.CityCoordinatesConstants
import com.gorman.common.data.LocationProvider
import com.gorman.common.models.CityData
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class GeoRepository @Inject constructor(
    private val locationProvider: LocationProvider,
    private val dataStoreManager: DataStoreManager,
    private val searchManager: SearchManager
) : IGeoRepository {

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
                        continuation.resume(
                            CityData(
                                city = cityEnum,
                                latitude = location.latitude,
                                longitude = location.longitude
                            )
                        )
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

                override fun onSearchError(p0: com.yandex.runtime.Error) {
                    continuation.resume(null)
                }
            }
        )
        continuation.invokeOnCancellation {
            searchSession.cancel()
        }
    }

    override suspend fun getUserLocation(): Result<Point> =
        locationProvider.getLastKnownLocation()

    override fun getDistanceFromPoints(
        point1: Point,
        point2: Point
    ): Int =
        Geo.distance(point1, point2).toInt() / 1000

    override fun getSavedCity(): Flow<CityData?> {
        return dataStoreManager.savedCity
    }

    override suspend fun saveCity(cityData: CityData) {
        dataStoreManager.saveCity(cityData)
    }
}
