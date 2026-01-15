package com.gorman.common.constants

enum class CityCoordinatesConstants(val cityName: String) {
    MINSK("minsk"),
    BREST("brest"),
    GRODNO("grodno"),
    GOMEL("gomel"),
    VITEBSK("vitebsk"),
    MOGILEV("mogilev");

    companion object {
        val cityCoordinatesList = listOf(
            MINSK, BREST, GRODNO, GOMEL, VITEBSK, MOGILEV
        )
    }
}
