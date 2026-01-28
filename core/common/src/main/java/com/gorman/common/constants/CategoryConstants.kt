package com.gorman.common.constants

import androidx.compose.runtime.Composable
import com.gorman.common.R
import androidx.compose.ui.res.stringResource

enum class CategoryConstants(val value: String) {
    MUSIC("music"),
    SPORT("sport"),
    EDUCATION("education"),
    ART("art"),
    FOOD("food"),
    CINEMA("cinema")
}

@Composable
fun CategoryConstants.toDisplayName(): String {
    return when (this) {
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
    val categoryEnum = try {
        CategoryConstants.valueOf(category.uppercase())
    } catch (_: IllegalArgumentException) {
        CategoryConstants.EDUCATION
    }
    return categoryEnum.toDisplayName()
}
