package com.gorman.feature.details.impl.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.gorman.feature.details.api.DetailsScreenNavKey
import com.gorman.feature.details.impl.ui.screens.DetailsEventScreenEntry
import com.gorman.feature.details.impl.ui.viewmodels.DetailsViewModel

fun EntryProviderScope<NavKey>.featureDetailsEntryBuilder() {
    entry<DetailsScreenNavKey> { key ->
        val viewModel = hiltViewModel<DetailsViewModel, DetailsViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(key)
            }
        )
        DetailsEventScreenEntry(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            detailsViewModel = viewModel
        )
    }
}
