package com.gorman.feature.events.impl.ui.mappers

import com.gorman.domainmodel.PointDomain
import com.gorman.feature.events.impl.ui.states.PointUiState

fun PointDomain.toUiState(): PointUiState = PointUiState(
    latitude = latitude,
    longitude = longitude
)

fun PointUiState.toDomain(): PointDomain = PointDomain(
    latitude = latitude,
    longitude = longitude
)
