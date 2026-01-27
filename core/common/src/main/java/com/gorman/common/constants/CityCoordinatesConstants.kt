package com.gorman.common.constants

enum class CityCoordinatesConstants(val cityName: String) {
    MINSK("minsk"),
    BREST("brest"),
    GRODNO("grodna"),
    GOMEL("gomiel"),
    VITEBSK("viciebsk"),
    MOGILEV("magiliow");

    companion object {
        fun fromCityName(name: String?): CityCoordinatesConstants? {
            return entries.find { it.cityName.equals(name, ignoreCase = true) }
        }
    }
}
