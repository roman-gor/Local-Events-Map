package com.gorman.localeventsmap.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import kotlinx.collections.immutable.ImmutableList

@Composable
fun LocalEventsMapNavigation(
    entries: ImmutableList<NavEntry<NavKey>>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(true) {
        onBack()
    }
    Box(
        modifier = modifier
    ) {
        NavDisplay(
            entries = entries,
            modifier = Modifier.fillMaxSize(),
            onBack = onBack
        )
    }
}
