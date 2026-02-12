package com.gorman.navigation.navigator

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavKey
import com.gorman.feature.events.api.HomeScreenNavKey
import com.gorman.navigation.state.NavigationState

class Navigator(val state: NavigationState) {
    fun navigateTo(key: NavKey) {
        if (key in state.backStacks) {
            state.currentTab = key
            Log.d("STACK", "Navigate: ${state.currentBackStack.toList()}")
        } else {
            state.currentBackStack.add(key)
            Log.d("STACK", "Navigate: ${state.currentBackStack.toList()}")
        }
    }

    fun goBack(): Boolean {
        val stack = state.currentBackStack
        Log.d("STACK", "Go back: ${state.currentBackStack.toList()}")
        return if (stack.size > 1) {
            stack.removeAt(stack.lastIndex)
            Log.d("STACK", "Go back: ${state.currentBackStack.toList()}")
            true
        } else if (state.currentTab != HomeScreenNavKey) {
            state.currentTab = HomeScreenNavKey
            Log.d("STACK", "Go back: ${state.currentBackStack.toList()}")
            true
        } else {
            Log.d("STACK", "Go back: ${state.currentBackStack.toList()}")
            false
        }
    }

    fun setRoot(key: NavKey) {
        if (key in state.backStacks) {
            state.currentTab = key
            popToRoot()
            Log.d("STACK", "Set Root: ${state.currentBackStack.toList()}")
            return
        }

        val stack = state.currentBackStack
        while (stack.isNotEmpty()) {
            stack.removeAt(stack.lastIndex)
        }
        stack.add(key)
        Log.d("STACK", "Set root: ${state.currentBackStack.toList()}")
    }

    fun popToRoot() {
        if (state.currentBackStack.size > 1) {
            while (state.currentBackStack.size > 1) {
                state.currentBackStack.removeAt(state.currentBackStack.lastIndex)
            }
        }
    }

    fun switchTab(key: NavKey) {
        if (key == state.currentTab) {
            popToRoot()
        } else {
            navigateTo(key)
        }
    }
}

@SuppressLint("ComposeCompositionLocalUsage")
val LocalNavigator = staticCompositionLocalOf<Navigator> {
    error("LocalNavigator not provided! Wrap your content in CompositionLocalProvider.")
}
