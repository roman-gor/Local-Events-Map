package com.gorman.database.mappers

import com.gorman.common.models.SettingsState
import com.gorman.database.data.model.SettingsEntity

fun SettingsEntity.toDomain() = SettingsState(
    userId = userId,
    cityData = cityData,
    filtersState = filtersState
)

fun SettingsState.toEntity() = SettingsEntity(
    userId = userId,
    cityData = cityData,
    filtersState = filtersState
)
