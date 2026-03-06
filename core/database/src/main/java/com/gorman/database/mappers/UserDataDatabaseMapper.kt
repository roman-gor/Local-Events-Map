package com.gorman.database.mappers

import com.gorman.common.models.CityData
import com.gorman.common.models.DateFilterState
import com.gorman.common.models.FiltersState
import com.gorman.database.data.model.UserCitySettingsEntity
import com.gorman.database.data.model.UserDataEntity
import com.gorman.database.data.model.UserFiltersEntity
import com.gorman.domainmodel.UserData

fun UserDataEntity.toDomain(): UserData =
    UserData(
        uid = uid,
        email = email,
        username = username
    )

fun UserData.toEntity(): UserDataEntity =
    UserDataEntity(
        uid = uid,
        email = email,
        username = username
    )

fun UserFiltersEntity.toDomain(): FiltersState =
    FiltersState(
        categories = categories,
        dateRange = DateFilterState(
            type = type,
            startDate = startDate,
            endDate = endDate
        ),
        distance = distance,
        isFree = isFree,
        name = name
    )

fun FiltersState.toEntity(userId: String): UserFiltersEntity =
    UserFiltersEntity(
        userId = userId,
        categories = categories,
        type = dateRange.type,
        startDate = dateRange.startDate,
        endDate = dateRange.endDate,
        distance = distance,
        isFree = isFree,
        name = name
    )

fun UserCitySettingsEntity.toDomain(): CityData =
    CityData(
        city = city,
        cityName = cityName,
        latitude = latitude,
        longitude = longitude
    )

fun CityData.toEntity(userId: String): UserCitySettingsEntity =
    UserCitySettingsEntity(
        userId = userId,
        city = city,
        cityName = cityName,
        latitude = latitude,
        longitude = longitude
    )
