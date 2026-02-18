package com.gorman.map.search

import android.util.Log
import com.gorman.common.constants.CityCoordinates
import com.gorman.common.models.CityData
import com.gorman.domainmodel.PointDomain
import com.gorman.map.mapper.toYandex
import com.yandex.mapkit.geometry.Geo
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

                    Log.d("CityName", cityName.orEmpty())

                    var cityEnum = cityName?.let { CityCoordinates.fromCityName(it) }

                    if (cityEnum == null) {
                        cityEnum = findNearestCity(point)
                    }

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

    private fun findNearestCity(userPoint: PointDomain): CityCoordinates? {
        return CityCoordinates.entries.minByOrNull { city ->
            val cityLocation = Point(city.cityCenter.latitude, city.cityCenter.longitude)
            Geo.distance(userPoint.toYandex(), cityLocation)
        }
    }
}
