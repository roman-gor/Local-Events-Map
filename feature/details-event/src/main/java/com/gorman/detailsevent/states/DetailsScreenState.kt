package com.gorman.detailsevent.states

import com.gorman.ui.states.MapUiEvent

sealed interface DetailsScreenState {
    object Loading : DetailsScreenState
    sealed interface Error : DetailsScreenState {
        data class NoNetwork(val message: String?) : Error
        data class NotFound(val eventId: String) : Error
        data class Unknown(val exception: Throwable) : Error
    }
    data class Success(val event: MapUiEvent) : DetailsScreenState
}
sealed interface DetailsScreenUiEvent {
    data class OnFavouriteClick(val id: String) : DetailsScreenUiEvent
}
