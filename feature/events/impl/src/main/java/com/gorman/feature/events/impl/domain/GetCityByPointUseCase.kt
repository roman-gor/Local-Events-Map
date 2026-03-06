package com.gorman.feature.events.impl.domain

import com.gorman.common.models.CityData
import com.gorman.domainmodel.PointDomain
import com.gorman.map.search.IMapSearchManager
import javax.inject.Inject

class GetCityByPointUseCase @Inject constructor(
    private val mapSearchManager: IMapSearchManager
) {
    suspend operator fun invoke(location: PointDomain): CityData? =
        mapSearchManager.getCityByPoint(location)
}
