package com.gorman.navigation.navigator

import androidx.navigation3.runtime.NavKey

sealed interface NavIntent {
    data class NavigateTo(val key: NavKey) : NavIntent
    data class SwitchTab(val key: NavKey) : NavIntent
    data class SetRoot(val key: NavKey) : NavIntent
    data object GoBack : NavIntent
    data object PopToRoot : NavIntent
}
