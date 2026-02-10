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

//    fun popToRoot() {
//        val root = _backStack.value.firstOrNull() ?: return
//        _backStack.value = listOf(root)
//    }
//
//    fun resetToRoot() {
//        popToRoot()
//    }

    fun setRoot(destination: NavKey) {
        backStack.clear()
        backStack.add(destination)
    }

    fun goBack() {
        backStack.removeLastOrNull()
    }
}
