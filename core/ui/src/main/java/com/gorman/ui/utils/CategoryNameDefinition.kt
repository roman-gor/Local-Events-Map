package com.gorman.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gorman.common.constants.CategoryConstants
import com.gorman.ui.R

@Composable
fun categoryNameDefinition(category: CategoryConstants): String {
    return when (category) {
        CategoryConstants.EDUCATION -> stringResource(R.string.education)
        CategoryConstants.MUSIC -> stringResource(R.string.music)
        CategoryConstants.ART -> stringResource(R.string.art)
        CategoryConstants.SPORT -> stringResource(R.string.sport)
        CategoryConstants.FOOD -> stringResource(R.string.food)
        CategoryConstants.CINEMA -> stringResource(R.string.cinema)
    }
}

@Composable
fun categoryNameDefinition(category: String): String {
    return when (category) {
        "education" -> stringResource(R.string.education)
        "music" -> stringResource(R.string.music)
        "art" -> stringResource(R.string.art)
        "sport" -> stringResource(R.string.sport)
        "food" -> stringResource(R.string.food)
        "cinema" -> stringResource(R.string.cinema)
        else -> stringResource(R.string.categoryPlaceholder)
    }
}
