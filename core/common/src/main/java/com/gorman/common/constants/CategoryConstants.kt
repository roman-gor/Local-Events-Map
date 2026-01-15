package com.gorman.common.constants

enum class CategoryConstants(val value: String) {
    MUSIC("music"),
    SPORT("sport"),
    EDUCATION("education"),
    ART("art"),
    FOOD("food"),
    CINEMA("cinema");

    companion object {
        val categoriesList = listOf(
            MUSIC,
            SPORT,
            EDUCATION,
            ART,
            FOOD,
            CINEMA
        )
    }
}
