package com.gorman.navigation.navigator

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey
import dagger.hilt.android.scopes.ActivityRetainedScoped

/**
 * Handles navigation events (forward and back) by updating the navigation state.
 */
@ActivityRetainedScoped
class Navigator(startDestination: NavKey) {
    val backStack: SnapshotStateList<NavKey> = mutableStateListOf(startDestination)

    fun goTo(destination: NavKey) {
        backStack.add(destination)
    }

    fun popUpTo(route: NavKey, inclusive: Boolean) {
        val index = backStack.indexOfLast { it == route }

        if (index == -1) return

        val targetSize = if (inclusive) index else index + 1

        while (backStack.size > targetSize) {
            backStack.removeLast()
        }
    }

    fun goBack() {
        backStack.removeLastOrNull()
    }
}
