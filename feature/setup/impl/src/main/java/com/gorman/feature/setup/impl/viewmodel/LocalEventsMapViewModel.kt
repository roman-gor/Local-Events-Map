package com.gorman.feature.setup.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.data.repository.user.IUserRepository
import com.gorman.feature.setup.impl.navigation.SetupNavDelegate
import com.gorman.feature.setup.impl.states.SetupScreenState
import com.gorman.feature.setup.impl.states.SetupScreenUiEvent
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
import kotlinx.coroutines.flow.stateIn

@HiltViewModel(assistedFactory = LocalEventsMapViewModel.Factory::class)
class LocalEventsMapViewModel @AssistedInject constructor(
    userRepository: IUserRepository,
    @Assisted val navigator: SetupNavDelegate
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(navigator: SetupNavDelegate): LocalEventsMapViewModel
    }

    private val retryTrigger = MutableSharedFlow<Unit>(replay = 1).apply {
        tryEmit(Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<SetupScreenState> = retryTrigger
        .flatMapLatest {
            userRepository
                .getUserData().map { it?.uid }
                .map { id ->
                    if (!id.isNullOrEmpty()) {
                        navigator.setHomeRoot()
                    } else {
                        navigator.setSignInRoot()
                    }
                    SetupScreenState.Loading
                }
        }.catch { e ->
            SetupScreenState.Error(e)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = SetupScreenState.Loading
        )

    fun onUiEvent(uiEvent: SetupScreenUiEvent) {
        when (uiEvent) {
            SetupScreenUiEvent.TryAgain -> retryTrigger.tryEmit(Unit)
        }
    }
}
