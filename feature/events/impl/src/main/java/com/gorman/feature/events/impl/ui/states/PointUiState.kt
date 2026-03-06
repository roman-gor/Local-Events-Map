package com.gorman.feature.events.impl.ui.states

import androidx.compose.runtime.Immutable

@Immutable
data class PointUiState(
    val latitude: Double,
    val longitude: Double
)
