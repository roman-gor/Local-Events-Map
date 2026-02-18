package com.gorman.common.constants

import com.gorman.common.R
import com.gorman.domainmodel.PointDomain
import java.lang.IllegalArgumentException

enum class CityCoordinates(
    val cityName: String,
    val resource: Int,
    val cityCenter: PointDomain
) {
    MINSK("minsk", R.string.minsk, PointDomain(53.8955, 27.5477)),
    BREST("brest", R.string.brest, PointDomain(52.0939, 23.6936)),
    GRODNA("grodna", R.string.grodno, PointDomain(53.6778, 23.8294)),
    GOMIEL("gomiel", R.string.gomel, PointDomain(52.4241, 31.0142)),
    VICIEBSK("viciebsk", R.string.mogilev, PointDomain(55.1842, 30.2017)),
    MAGILIOW("magiliow", R.string.vitebsk, PointDomain(53.8945, 30.3304));

    companion object {
        fun fromCityName(name: String?): CityCoordinates? {
            return entries.find { it.cityName.equals(name, ignoreCase = true) }
        }
    }
}

fun cityNameDefinition(city: String): Int {
    val cityEnum = try {
        CityCoordinates.valueOf(city.uppercase())
    } catch (_: IllegalArgumentException) {
        CityCoordinates.MINSK
    }
    return cityEnum.resource
}
