package com.gorman.map.mapper

import com.gorman.domainmodel.PointDomain
import com.yandex.mapkit.geometry.Point

fun Point.toDomain(): PointDomain = PointDomain(
    latitude = latitude,
    longitude = longitude
)

fun PointDomain.toYandex(): Point = Point(
    this.latitude,
    this.longitude
)
