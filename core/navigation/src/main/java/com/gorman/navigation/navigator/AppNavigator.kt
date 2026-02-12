package com.gorman.navigation.navigator

import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class AppNavigator @Inject constructor() : IAppNavigator {
    private val _navigationEvents = Channel<NavIntent>(Channel.BUFFERED)
    override val navigationEvents = _navigationEvents.receiveAsFlow()

    override fun navigateTo(key: NavKey) {
        _navigationEvents.trySend(NavIntent.NavigateTo(key))
    }

    override fun switchTab(key: NavKey) {
        _navigationEvents.trySend(NavIntent.SwitchTab(key))
    }

    override fun setRoot(key: NavKey) {
        _navigationEvents.trySend(NavIntent.SetRoot(key))
    }

    override fun goBack() {
        _navigationEvents.trySend(NavIntent.GoBack)
    }

    override fun popToRoot() {
        _navigationEvents.trySend(NavIntent.PopToRoot)
    }
}
