package com.gorman.events.ui.states

import androidx.compose.runtime.Immutable

@Immutable
data class MapUiEvent(
    val id: String = "",
    val name: String? = null,
    val category: String? = null,
    val photoUrl: String? = null,
    val coordinates: String? = null,
    val isSelected: Boolean = false,
    val isFavourite: Boolean? = null
)
