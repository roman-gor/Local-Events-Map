package com.gorman.feature.events.impl.states

import androidx.compose.runtime.Immutable

@Immutable
data class MapUiEvent(
    val id: String = "",
    val name: String? = null,
    val description: String? = null,
    val category: String? = null,
    val photoUrl: String? = null,
    val date: Long? = null,
    val address: String? = null,
    val coordinates: String? = null,
    val isSelected: Boolean = false,
    val isFavourite: Boolean? = null
)
