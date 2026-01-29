package com.gorman.localeventsmap.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.gorman.navigation.navigator.Navigator

@Composable
fun LocalEventsMapNavigation(
    navigator: Navigator,
    @SuppressLint("ComposeUnstableCollections")
    entryBuilders: Set<@JvmSuppressWildcards EntryProviderScope<NavKey>.() -> Unit>,
    modifier: Modifier = Modifier
) {
    NavDisplay(
        backStack = navigator.backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        modifier = modifier,
        onBack = { navigator.goBack() },
        entryProvider = entryProvider { entryBuilders.forEach { builder -> this.builder() } }
    )
}
