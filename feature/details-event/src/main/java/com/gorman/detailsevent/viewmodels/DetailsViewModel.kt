package com.gorman.detailsevent.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.data.repository.mapevents.IMapEventsRepository
import com.gorman.data.repository.user.IUserRepository
import com.gorman.detailsevent.states.DetailsScreenState
import com.gorman.detailsevent.states.DetailsScreenUiEvent
import com.gorman.ui.mappers.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    mapEventsRepository: IMapEventsRepository,
    private val userRepository: IUserRepository
) : ViewModel() {
    private val id = "event2"

    val uiState: StateFlow<DetailsScreenState> = combine(
        mapEventsRepository.getEventById(id),
        userRepository.getUserFavouriteEvents()
    ) { event, favoriteEvents ->
        val s = DetailsScreenState.Success(event.toUiState()
            .copy(isFavourite = event.id in favoriteEvents)) as DetailsScreenState
        Log.d("Ids check", "${s}, $favoriteEvents")
        s
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
            userRepository.updateFavouriteEventsState(id).onFailure { e ->
                Log.e("Details VM", "Error updating state of favourite: ${e.message}")
            }
        }
    }
}
