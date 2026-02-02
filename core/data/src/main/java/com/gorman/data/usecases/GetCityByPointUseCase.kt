package com.gorman.data.usecases

import com.gorman.common.constants.CityCoordinatesConstants
import com.gorman.common.models.CityData
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

class GetCityByPointUseCase @Inject constructor(
    private val searchManager: SearchManager
) {
    suspend operator fun invoke(location: Point) = suspendCancellableCoroutine { continuation ->
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
