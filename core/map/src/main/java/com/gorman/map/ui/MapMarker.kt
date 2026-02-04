package com.gorman.map.ui

import androidx.annotation.DrawableRes

data class MapMarker(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val isSelected: Boolean = false,
    @param:DrawableRes val iconRes: Int,
    @param:DrawableRes val selectedIconRes: Int
)
