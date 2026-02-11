package com.gorman.feature.events.impl.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.gorman.feature.events.api.HomeScreenNavKey
import com.gorman.feature.events.impl.ui.screens.mapscreen.MapScreenEntry

fun EntryProviderScope<NavKey>.featureEventsEntryBuilder() {
    entry<HomeScreenNavKey> {
        MapScreenEntry(modifier = Modifier.fillMaxSize())
    }
}
