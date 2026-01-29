package com.gorman.feature.details.impl.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.data.repository.mapevents.IMapEventsRepository
import com.gorman.data.repository.user.IUserRepository
import com.gorman.feature.details.api.DetailsScreenNavKey
import com.gorman.feature.details.impl.states.DetailsScreenState
import com.gorman.feature.details.impl.states.DetailsScreenUiEvent
import com.gorman.ui.mappers.toUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okio.IOException

@HiltViewModel(assistedFactory = DetailsViewModel.Factory::class)
class DetailsViewModel @AssistedInject constructor(
    mapEventsRepository: IMapEventsRepository,
    private val userRepository: IUserRepository,
    @Assisted val navKey: DetailsScreenNavKey
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(navKey: DetailsScreenNavKey): DetailsViewModel
    }

    private val _id = navKey.id

    val uiState: StateFlow<DetailsScreenState> = combine(
        mapEventsRepository.getEventById(_id),
        userRepository.getUserFavouriteEvents()
    ) { event, favoriteEvents ->
        val s = DetailsScreenState.Success(
            event.toUiState()
                .copy(isFavourite = event.id in favoriteEvents)
        ) as DetailsScreenState
        Log.d("Ids check", "$s, $favoriteEvents")
        s
    }.catch { e ->
        when (e) {
            is IOException -> emit(DetailsScreenState.Error.NoNetwork(e.message))
            is IllegalStateException -> emit(DetailsScreenState.Error.NotFound(_id))
            is NoSuchElementException -> emit(DetailsScreenState.Error.NotFound(_id))
            else -> emit(DetailsScreenState.Error.Unknown(e))
        }
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
