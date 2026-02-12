package com.gorman.feature.setup.impl.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.gorman.feature.setup.api.SetupScreenNavKey
import com.gorman.feature.setup.impl.screens.LocalEventsMapScreen
import com.gorman.feature.setup.impl.viewmodel.LocalEventsMapViewModel
import com.gorman.navigation.navigator.LocalNavigator

fun EntryProviderScope<NavKey>.featureSetupEntryBuilder() {
    entry<SetupScreenNavKey> {
        val navigator = LocalNavigator.current
        val viewModel = hiltViewModel<LocalEventsMapViewModel, LocalEventsMapViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(SetupNavDelegate(navigator))
            }
        )
        LocalEventsMapScreen(viewModel = viewModel)
    }
}
