package com.gorman.localeventsmap.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.deeplinks.DeeplinkHandlerUseCase
import com.gorman.deeplinks.states.DeepLinkResult
import com.gorman.deeplinks.strategies.EventId
import com.gorman.localeventsmap.R
import com.gorman.localeventsmap.states.MainUiState
import com.gorman.localeventsmap.states.MainUiState.NavigateToEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val deeplinkHandlerUseCase: DeeplinkHandlerUseCase
) : ViewModel() {

    private val _mainUiState = Channel<MainUiState>()
    val mainUiState = _mainUiState.receiveAsFlow()

    fun onDeepLinkReceived(link: String?) {
        if (link.isNullOrBlank()) return
        viewModelScope.launch(Dispatchers.Main.immediate) {
            handleDeepLinkResult(deeplinkHandlerUseCase(link))
        }
    }

    private suspend fun handleDeepLinkResult(result: DeepLinkResult<EventId>) {
        when (result) {
            is DeepLinkResult.Success<EventId> -> {
                _mainUiState.send(NavigateToEvent(result.data.id))
            }
            DeepLinkResult.NotFound -> R.string.eventNotFound
            DeepLinkResult.Network -> R.string.errorNetwork
            is DeepLinkResult.Error -> R.string.errorGeneric
            DeepLinkResult.Ignored -> Unit
            is DeepLinkResult.GenericError<*> -> Unit
        }
    }
}
