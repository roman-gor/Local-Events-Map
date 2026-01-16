package com.gorman.events.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gorman.common.constants.CityCoordinatesConstants
import com.gorman.events.R

@Composable
fun CityNameDefinition(city: CityCoordinatesConstants): String {
    return when (city) {
        CityCoordinatesConstants.MINSK -> stringResource(R.string.minsk)
        CityCoordinatesConstants.BREST -> stringResource(R.string.brest)
        CityCoordinatesConstants.GRODNO -> stringResource(R.string.grodno)
        CityCoordinatesConstants.GOMEL -> stringResource(R.string.gomel)
        CityCoordinatesConstants.MOGILEV -> stringResource(R.string.mogilev)
        CityCoordinatesConstants.VITEBSK -> stringResource(R.string.vitebsk)
    }}

@Composable
fun CityNameDefinition(cityName: String): String {
    return when (cityName) {
        "minsk" -> stringResource(R.string.minsk)
        "brest" -> stringResource(R.string.brest)
        "grodno" -> stringResource(R.string.grodno)
        "gomel" -> stringResource(R.string.gomel)
        "mogilev" -> stringResource(R.string.mogilev)
        "vitebsk" -> stringResource(R.string.vitebsk)
        else -> stringResource(R.string.minsk)
    }}
