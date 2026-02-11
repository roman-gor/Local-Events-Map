package com.gorman.navigation.navigator

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey
import dagger.hilt.android.scopes.ActivityRetainedScoped

/**
 * Handles navigation events (forward and back) by updating the navigation state.
 */
@ActivityRetainedScoped
class Navigator(private val startDestination: NavKey) {
    val backStack: SnapshotStateList<NavKey> = mutableStateListOf(startDestination)

    fun goTo(destination: NavKey) {
        backStack.add(destination)
    }

    fun setRoot(destination: NavKey) {
        backStack.clear()
        backStack.add(destination)
    }

    fun goBack() {
        backStack.removeLastOrNull()
    }

    fun popToRoot() {
        if (backStack.size > 1) {
            backStack.removeRange(1, backStack.size)
        }
    }

    fun switchTab(tab: NavKey) {
        if (backStack.lastOrNull() == tab) return

        popToRoot()

        if (tab != startDestination) {
            goTo(tab)
        }
    }
}
