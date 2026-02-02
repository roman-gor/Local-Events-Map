package com.gorman.feature.bookmarks.impl.states

sealed interface BookmarksScreenUiEvent {
    data object OnRetryClick : BookmarksScreenUiEvent
    data class OnEventClick(val eventId: String) : BookmarksScreenUiEvent
    data class ChangeLikeState(val eventId: String) : BookmarksScreenUiEvent
    data object OnExploreClick : BookmarksScreenUiEvent
    data object OnSignOutClick : BookmarksScreenUiEvent
    data object OnSignInClick : BookmarksScreenUiEvent
}
