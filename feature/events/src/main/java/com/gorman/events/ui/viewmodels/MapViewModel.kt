package com.gorman.events.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.common.domain.usecases.GetAllMapEventsUseCase
import com.gorman.common.domain.usecases.SyncMapEventsFromRemoteUseCase
import com.gorman.domain_model.MapEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val syncMapEventsFromRemoteUseCase: SyncMapEventsFromRemoteUseCase,
    private val getAllMapEventsUseCase: GetAllMapEventsUseCase
): ViewModel() {
    private val _eventsListState = MutableStateFlow<List<MapEvent>>(emptyList())
    val eventsListState = _eventsListState.asStateFlow()

    private val _selectedMapEventId = MutableStateFlow<MapEvent?>(null)
    val selectedEventId = _selectedMapEventId.asStateFlow()

    fun syncEvents() {
        viewModelScope.launch {
            Log.d("ViewModel", "Вызов syncEvents()")
            syncMapEventsFromRemoteUseCase()
        }
    }

    fun getEventsList() {
        viewModelScope.launch {
            getAllMapEventsUseCase().collect { events ->
                _eventsListState.value = events
            }
        }
    }

    fun selectEvent(id: Int) {
        viewModelScope.launch {
            _selectedMapEventId.value = _eventsListState.value.first { it.localId == id }
        }
    }
}
