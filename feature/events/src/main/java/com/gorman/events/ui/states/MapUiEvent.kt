package com.gorman.events.ui.states

import androidx.compose.runtime.Immutable

@Immutable
data class MapUiEvent(
    val id: Int,
    val name: String? = null,
    val category: String? = null,
    val photoUrl: String? = null,
    val isSelected: Boolean? = null,
    val isFavourite: Boolean? = null
)
