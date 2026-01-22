package com.gorman.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gorman.common.constants.CityCoordinatesConstants
import com.gorman.ui.R

@Composable
fun cityNameDefinition(city: CityCoordinatesConstants): String {
    return when (city) {
        CityCoordinatesConstants.MINSK -> stringResource(R.string.minsk)
        CityCoordinatesConstants.BREST -> stringResource(R.string.brest)
        CityCoordinatesConstants.GRODNO -> stringResource(R.string.grodno)
        CityCoordinatesConstants.GOMEL -> stringResource(R.string.gomel)
        CityCoordinatesConstants.MOGILEV -> stringResource(R.string.mogilev)
        CityCoordinatesConstants.VITEBSK -> stringResource(R.string.vitebsk)
    }
}

@Composable
fun cityNameDefinition(city: String): String {
    return when (city) {
        "minsk" -> stringResource(R.string.minsk)
        "brest" -> stringResource(R.string.brest)
        "grodna" -> stringResource(R.string.grodno)
        "gomiel" -> stringResource(R.string.gomel)
        "magiliow" -> stringResource(R.string.mogilev)
        "viciebsk" -> stringResource(R.string.vitebsk)
        else -> stringResource(R.string.cityPlaceholder)
    }
}
