package com.gorman.feature.setup.impl.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gorman.feature.setup.impl.R
import com.gorman.feature.setup.impl.states.SetupScreenState
import com.gorman.feature.setup.impl.states.SetupScreenUiEvent
import com.gorman.feature.setup.impl.viewmodel.LocalEventsMapViewModel
import com.gorman.ui.components.ErrorDataScreen
import com.gorman.ui.components.LoadingIndicator

@SuppressLint("ComposeModifierMissing")
@Composable
fun LocalEventsMapScreen(
    viewModel: LocalEventsMapViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    when (uiState) {
        is SetupScreenState.Error -> ErrorDataScreen(
            text = stringResource(R.string.errorDataLoading),
            onRetryClick = { viewModel.onUiEvent(SetupScreenUiEvent.TryAgain) },
            modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)
        )
        SetupScreenState.Loading -> LoadingIndicator(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        )
    }
}
