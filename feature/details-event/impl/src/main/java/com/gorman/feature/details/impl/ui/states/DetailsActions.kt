package com.gorman.feature.details.impl.ui.states

import androidx.compose.runtime.Immutable

@Immutable
data class DetailsActions(
    val onFavouriteClick: () -> Unit,
    val onLocationClick: () -> Unit,
    val onShareClick: () -> Unit,
    val onLinkClick: () -> Unit
)
