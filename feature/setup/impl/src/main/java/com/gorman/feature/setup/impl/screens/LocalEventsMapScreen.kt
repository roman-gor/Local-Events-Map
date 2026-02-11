package com.gorman.feature.setup.impl.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gorman.feature.setup.impl.R
import com.gorman.feature.setup.impl.states.SetupScreenState
import com.gorman.feature.setup.impl.states.SetupScreenUiEvent
import com.gorman.feature.setup.impl.viewmodel.LocalEventsMapViewModel
import com.gorman.ui.components.ErrorDataScreen
import com.gorman.ui.components.LoadingStub

@Composable
fun LocalEventsMapScreen(
    viewModel: LocalEventsMapViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    when (uiState) {
        is SetupScreenState.Error -> ErrorDataScreen(
            text = stringResource(R.string.errorDataLoading),
            onRetryClick = { viewModel.onUiEvent(SetupScreenUiEvent.TryAgain) }
        )
        SetupScreenState.Loading -> LoadingStub()
    }
}
