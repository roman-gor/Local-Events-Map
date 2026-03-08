package com.gorman.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavKey
import com.gorman.feature.auth.api.SignInScreenNavKey
import com.gorman.feature.events.api.HomeScreenNavKey

class Navigator(val state: NavigationState) {
    fun navigate(key: NavKey) {
        when (key) {
            state.currentTopLevelKey -> clearSubStack()
            in state.topLevelKeys -> goToTopLevel(key)
            else -> goToKey(key)
        }
    }

    fun goBack(): Boolean {
        return when (state.currentKey) {
            SignInScreenNavKey -> false
            HomeScreenNavKey -> false
            state.startKey -> {
                false
            }
            state.currentTopLevelKey -> {
                state.topLevelStack.removeLastOrNull()
                true
            }
            else -> {
                state.currentSubStack.removeLastOrNull()
                true
            }
        }
    }

    private fun goToKey(key: NavKey) {
        state.currentSubStack.apply {
            remove(key)
            add(key)
        }
    }

    private fun goToTopLevel(key: NavKey) {
        state.topLevelStack.apply {
            if (key == state.startKey) {
                clear()
            } else {
                remove(key)
            }
            add(key)
        }
    }

    private fun clearSubStack() {
        state.currentSubStack.run {
            if (size > 1) subList(1, size).clear()
        }
    }
}

@SuppressLint("ComposeCompositionLocalUsage")
val LocalNavigator = staticCompositionLocalOf<Navigator> {
    error("LocalNavigator not provided! Wrap your content in CompositionLocalProvider.")
}
