package com.gorman.common.constants

import androidx.compose.runtime.Composable
import com.gorman.common.R
import androidx.compose.ui.res.stringResource
import java.lang.IllegalArgumentException

enum class CityCoordinatesConstants(val cityName: String) {
    MINSK("minsk"),
    BREST("brest"),
    GRODNA("grodna"),
    GOMIEL("gomiel"),
    VICIEBSK("viciebsk"),
    MAGILIOW("magiliow");

    companion object {
        val cityCoordinatesList = listOf(
            MINSK,
            BREST,
            GRODNA,
            GOMIEL,
            VICIEBSK,
            MAGILIOW
        )
        fun fromCityName(name: String?): CityCoordinatesConstants? {
            return entries.find { it.cityName.equals(name, ignoreCase = true) }
        }
    }
}

fun CityCoordinatesConstants.toDisplayName(): String {
    return when(this) {
        CityCoordinatesConstants.MINSK -> stringResource(R.string.minsk)
        CityCoordinatesConstants.BREST -> stringResource(R.string.brest)
        CityCoordinatesConstants.GRODNA -> stringResource(R.string.grodno)
        CityCoordinatesConstants.GOMIEL -> stringResource(R.string.gomel)
        CityCoordinatesConstants.MAGILIOW -> stringResource(R.string.mogilev)
        CityCoordinatesConstants.VICIEBSK -> stringResource(R.string.vitebsk)
    }
}

@Composable
fun cityNameDefinition(city: String): String {
    val cityEnum = try {
        CityCoordinatesConstants.valueOf(city.uppercase())
    } catch(_: IllegalArgumentException) {
        CityCoordinatesConstants.MINSK
    }
    return cityEnum.toDisplayName()
}
