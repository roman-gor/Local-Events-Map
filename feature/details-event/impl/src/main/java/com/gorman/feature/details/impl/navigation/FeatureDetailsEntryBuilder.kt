package com.gorman.feature.details.impl.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.gorman.feature.details.api.DetailsScreenNavKey
import com.gorman.feature.details.impl.ui.screens.DetailsEventScreenEntry
import com.gorman.feature.details.impl.ui.viewmodels.DetailsViewModel

fun EntryProviderScope<NavKey>.featureDetailsEntryBuilder() {
    entry<DetailsScreenNavKey>(
        metadata = NavDisplay.predictivePopTransitionSpec {
            fadeIn(animationSpec = tween(300)) togetherWith
                fadeOut(animationSpec = tween(300))
        } + NavDisplay.popTransitionSpec {
            fadeIn(animationSpec = tween(300)) togetherWith
                fadeOut(animationSpec = tween(300))
        }
    ) { key ->
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
