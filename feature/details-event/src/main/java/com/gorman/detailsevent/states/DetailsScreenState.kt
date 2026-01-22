package com.gorman.detailsevent.states

import com.gorman.ui.states.MapUiEvent

sealed interface DetailsScreenState {
    object Loading : DetailsScreenState
    data class Error(val e: Throwable) : DetailsScreenState
    data class Success(val event: MapUiEvent) : DetailsScreenState
}
sealed interface DetailsScreenUiEvent {
    data class OnFavouriteClick(val id: String) : DetailsScreenUiEvent
}
