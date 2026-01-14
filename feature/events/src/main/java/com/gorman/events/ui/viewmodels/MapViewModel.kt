package com.gorman.events.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.common.domain.usecases.GetAllMapEventsUseCase
import com.gorman.common.domain.usecases.SyncMapEventsFromRemoteUseCase
import com.gorman.domain_model.MapEvent
import com.gorman.events.ui.states.FiltersState
import com.gorman.events.ui.states.MapEventsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.toMutableList

@HiltViewModel
class MapViewModel @Inject constructor(
    private val syncMapEventsFromRemoteUseCase: SyncMapEventsFromRemoteUseCase,
    private val getAllMapEventsUseCase: GetAllMapEventsUseCase
): ViewModel() {
    private val _mapEventsState = MutableStateFlow<MapEventsState>(MapEventsState.Idle)
    val mapEventState = _mapEventsState.asStateFlow()

    private val _filterState = MutableStateFlow(FiltersState())
    val filterState = _filterState.asStateFlow()

    private val _selectedMapEventId = MutableStateFlow<MapEvent?>(null)
    val selectedEventId = _selectedMapEventId.asStateFlow()

    fun syncEvents() {
        viewModelScope.launch {
            syncMapEventsFromRemoteUseCase()
        }
    }

    fun getEventsList() {
        _mapEventsState.value = MapEventsState.Loading
        viewModelScope.launch {
            getAllMapEventsUseCase()
                .onSuccess { eventsFlow ->
                    eventsFlow.combine(_filterState) { events, filters ->
                        if (filters.categories.isEmpty()) {
                            events
                        }
                        else {
                            events.filter { event ->
                                val categoryMatch = event.category in filters.categories
                                categoryMatch
                            }
                        }
                    }
                    .collectLatest {
                        _mapEventsState.value = MapEventsState.Success(it)
                    }
                }
                .onFailure { exception ->
                    _mapEventsState.value = MapEventsState.Error(exception)
                }
        }
    }

    fun onCategoryChanged(category: String) {
        val currentCategories = _filterState.value.categories.toMutableList()
        if (currentCategories.contains(category)) {
            currentCategories.remove(category)
        } else {
            currentCategories.add(category)
        }
        Log.d("FilterList", _filterState.value.categories.toString())
        _filterState.value = _filterState.value.copy(categories = currentCategories)
    }

    fun selectEvent(id: Int) {
        viewModelScope.launch {
            val state = _mapEventsState.value as MapEventsState.Success
            _selectedMapEventId.value = state.eventsList.first { it.localId == id }
        }
    }
}
