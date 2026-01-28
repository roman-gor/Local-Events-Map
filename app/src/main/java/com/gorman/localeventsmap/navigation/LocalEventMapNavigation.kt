package com.gorman.localeventsmap.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.gorman.navigation.navigator.Navigator

@Composable
fun LocalEventsMapNavigation(
    navigator: Navigator,
    entryBuilders: Set<EntryProviderScope<NavKey>.() -> Unit>,
    modifier: Modifier = Modifier
) {
    NavDisplay(
        backStack = navigator.backStack,
        modifier = modifier,
        onBack = { navigator.goBack() },
        entryProvider = entryProvider { entryBuilders.forEach { builder -> this.builder() } }
    )
}
