package com.gorman.events.ui.constants

import com.gorman.events.R

enum class CategoryConstants(val value: String) {
    MUSIC("music"),
    SPORT("sport"),
    EDUCATION("education"),
    ART("art"),
    FOOD("food"),
    CINEMA("cinema")
}

val categoriesList = listOf(
    CategoryConstants.MUSIC,
    CategoryConstants.SPORT,
    CategoryConstants.EDUCATION,
    CategoryConstants.ART,
    CategoryConstants.FOOD,
    CategoryConstants.CINEMA
)
