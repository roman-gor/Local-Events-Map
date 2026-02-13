package com.gorman.feature.setup.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.data.repository.user.IUserRepository
import com.gorman.feature.auth.api.SignInScreenNavKey
import com.gorman.feature.events.api.HomeScreenNavKey
import com.gorman.feature.setup.impl.states.SetupScreenState
import com.gorman.feature.setup.impl.states.SetupScreenUiEvent
import com.gorman.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LocalEventsMapViewModel @Inject constructor(
    userRepository: IUserRepository,
    private val navigator: Navigator
) : ViewModel() {

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
                        navigator.setRoot(HomeScreenNavKey)
                    } else {
                        navigator.setRoot(SignInScreenNavKey)
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
