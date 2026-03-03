package com.gorman.common.models

data class SettingsState(
    val userId: String,
    val cityData: CityData?,
    val filtersState: FiltersState?
)
