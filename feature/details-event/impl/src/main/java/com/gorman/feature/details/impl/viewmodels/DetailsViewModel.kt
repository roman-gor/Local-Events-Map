package com.gorman.feature.details.impl.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.data.repository.mapevents.IMapEventsRepository
import com.gorman.data.repository.mapevents.IMapEventsRepository
import com.gorman.data.repository.user.IUserRepository
import com.gorman.feature.details.api.DetailsScreenNavKey
import com.gorman.feature.details.impl.states.DetailsScreenState
import com.gorman.feature.details.impl.states.DetailsScreenUiEvent
import com.gorman.navigation.navigator.Navigator
import com.gorman.ui.mappers.toUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okio.IOException

@HiltViewModel(assistedFactory = DetailsViewModel.Factory::class)
class DetailsViewModel @AssistedInject constructor(
    private val mapEventsRepository: IMapEventsRepository,
    private val navigator: Navigator,
    private val userRepository: IUserRepository,
    @Assisted val navKey: DetailsScreenNavKey
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(navKey: DetailsScreenNavKey): DetailsViewModel
    }

    private val _id = navKey.id
    private val retryTrigger = MutableSharedFlow<Unit>(replay = 1).apply {
        tryEmit(Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DetailsScreenState> = retryTrigger
        .flatMapLatest {
            mapEventsRepository.getEventById(_id)
                .map { flowEvent ->
                    Log.d("ID", _id)
                    DetailsScreenState.Success(flowEvent.toUiState()) as DetailsScreenState
                }
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
            DetailsScreenUiEvent.OnRetryClick -> retryTrigger.tryEmit(Unit)
            DetailsScreenUiEvent.OnNavigateToBack -> navigator.goBack()
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
