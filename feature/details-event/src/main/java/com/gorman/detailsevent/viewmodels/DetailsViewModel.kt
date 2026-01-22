package com.gorman.detailsevent.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.data.repository.IMapEventsRepository
import com.gorman.detailsevent.states.DetailsScreenState
import com.gorman.detailsevent.states.DetailsScreenUiEvent
import com.gorman.ui.mappers.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val mapEventsRepository: IMapEventsRepository
) : ViewModel() {
    private val id = "event2"

    val uiState: StateFlow<DetailsScreenState> = mapEventsRepository.getEventById(id)
        .map { flowEvent ->
            DetailsScreenState.Success(flowEvent.toUiState()) as DetailsScreenState
        }.catch { e ->
            emit(DetailsScreenState.Error(e))
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DetailsScreenState.Loading
        )

    fun onUiEvent(uiEvent: DetailsScreenUiEvent) {
        when (uiEvent) {
            is DetailsScreenUiEvent.OnFavouriteClick -> onFavouriteChange(uiEvent.id)
        }
    }

    private fun onFavouriteChange(id: String) {
        viewModelScope.launch {
            val result = mapEventsRepository.updateFavouriteState(id)
            result.onFailure { e->
                Log.e("Details VM", "Error updating state of favourite ${e.message}")
            }
        }
    }
}
