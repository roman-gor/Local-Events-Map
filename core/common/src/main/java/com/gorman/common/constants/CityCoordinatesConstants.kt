package com.gorman.common.constants

enum class CityCoordinatesConstants(val cityName: String) {
    MINSK("minsk"),
    BREST("brest"),
    GRODNO("grodna"),
    GOMEL("gomiel"),
    VITEBSK("viciebsk"),
    MOGILEV("magiliow");

    companion object {
        val cityCoordinatesList = listOf(
            MINSK, BREST, GRODNO, GOMEL, VITEBSK, MOGILEV
        )
        fun fromCityName(name: String?): CityCoordinatesConstants? {
            return entries.find { it.cityName.equals(name, ignoreCase = true) }
        }
    }
}
