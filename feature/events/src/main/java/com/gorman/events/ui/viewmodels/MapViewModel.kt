package com.gorman.events.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.common.domain.usecases.GetAllEventsUseCase
import com.gorman.common.domain.usecases.SyncEventsFromRemoteUseCase
import com.gorman.domain_model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val syncEventsFromRemoteUseCase: SyncEventsFromRemoteUseCase,
    private val getAllEventsUseCase: GetAllEventsUseCase
): ViewModel() {
    private val _eventsListState = MutableStateFlow<List<Event>>(emptyList())
    val eventsListState = _eventsListState.asStateFlow()

    fun syncEvents() {
        viewModelScope.launch {
            Log.d("ViewModel", "Вызов syncEvents()")
            syncEventsFromRemoteUseCase()
        }
    }

    fun getEventsList() {
        viewModelScope.launch {
            getAllEventsUseCase().collect { events ->
                _eventsListState.value = events
            }
        }
    }
}
