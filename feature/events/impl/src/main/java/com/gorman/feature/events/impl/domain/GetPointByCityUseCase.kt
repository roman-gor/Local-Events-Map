package com.gorman.feature.events.impl.domain

import com.gorman.common.constants.CityCoordinates
import com.gorman.common.models.CityData
import com.gorman.map.search.IMapSearchManager
import javax.inject.Inject

class GetPointByCityUseCase @Inject constructor(
    private val mapSearchManager: IMapSearchManager
) {
    suspend operator fun invoke(city: CityCoordinates): CityData? =
        mapSearchManager.getPointByCity(city)
}
