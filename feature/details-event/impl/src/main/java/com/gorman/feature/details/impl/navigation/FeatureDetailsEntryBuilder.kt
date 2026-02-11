package com.gorman.feature.details.impl.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.gorman.feature.details.api.DetailsScreenNavKey
import com.gorman.feature.details.impl.screens.DetailsEventScreenEntry
import com.gorman.feature.details.impl.viewmodels.DetailsViewModel

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
