package com.gorman.feature.events.impl.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gorman.common.constants.CityCoordinatesConstants
import com.gorman.feature.events.impl.R

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
