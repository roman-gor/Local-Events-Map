package com.gorman.localeventsmap.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.data.repository.mapevents.IMapEventsRepository
import com.gorman.localeventsmap.states.MainUiSideEffects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mapEventsRepository: IMapEventsRepository
) : ViewModel() {

    private val _deepLinkSideEffect = Channel<MainUiSideEffects>()
    val deepLinkSideEffect = _deepLinkSideEffect.receiveAsFlow()

    fun handleDeepLink(intent: Intent?) {
        if (intent?.action != Intent.ACTION_VIEW) return
        val uri = intent.data ?: return

        if (uri.scheme == "app" && uri.host == "events") {
            val eventId = uri.lastPathSegment
            if (!eventId.isNullOrBlank()) {
                fetchEvent(eventId)
            }
        }
    }

    private fun fetchEvent(eventId: String) {
        viewModelScope.launch {
            val result = mapEventsRepository.syncEventById(eventId)

            if (result.isSuccess) {
                _deepLinkSideEffect.send(MainUiSideEffects.OnNavigateToDetails(eventId))
            } else {
                _deepLinkSideEffect.send(MainUiSideEffects.ShowErrorToast)
            }
        }
    }
}
