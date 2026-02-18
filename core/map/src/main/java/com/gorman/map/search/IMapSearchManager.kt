package com.gorman.map.search

import com.gorman.common.models.CityData
import com.gorman.domainmodel.PointDomain

interface IMapSearchManager {
    fun calculatingDistance(point1: PointDomain, point2: PointDomain): Int
    suspend fun getCityByPoint(point: PointDomain): CityData?
}
