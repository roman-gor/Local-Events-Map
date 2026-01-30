package com.gorman.feature.setup.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.gorman.feature.setup.api.SetupScreenNavKey
import com.gorman.feature.setup.impl.screens.LocalEventsMapScreen

fun EntryProviderScope<NavKey>.featureSetupEntryBuilder() {
    entry<SetupScreenNavKey> {
        LocalEventsMapScreen()
    }
}
