package com.gorman.data.usecases

import com.gorman.common.constants.CityCoordinatesConstants
import com.gorman.common.models.CityData
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class GetPointByCityUseCase @Inject constructor(
    private val searchManager: SearchManager
) {
    suspend operator fun invoke(city: CityCoordinatesConstants
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
}
