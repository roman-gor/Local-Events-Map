package com.gorman.common.constants

import com.gorman.common.R
import java.lang.IllegalArgumentException

enum class CityCoordinates(val cityName: String, val resource: Int) {
    MINSK("minsk", R.string.minsk),
    BREST("brest", R.string.brest),
    GRODNA("grodna", R.string.grodno),
    GOMIEL("gomiel", R.string.gomel),
    VICIEBSK("viciebsk", R.string.mogilev),
    MAGILIOW("magiliow", R.string.vitebsk);

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
