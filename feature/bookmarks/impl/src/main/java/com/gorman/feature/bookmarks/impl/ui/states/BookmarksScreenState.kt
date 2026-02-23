package com.gorman.feature.bookmarks.impl.ui.states

import androidx.compose.runtime.Stable
import com.gorman.ui.states.MapUiEvent
import com.gorman.ui.states.UserUiState
import kotlinx.collections.immutable.ImmutableList

@Stable
sealed interface BookmarksScreenState {
    object Loading : BookmarksScreenState
    data class Error(val e: Throwable) : BookmarksScreenState
    data class Success(
        val bookmarks: ImmutableList<MapUiEvent>,
        val userUiState: UserUiState
    ) : BookmarksScreenState
}
