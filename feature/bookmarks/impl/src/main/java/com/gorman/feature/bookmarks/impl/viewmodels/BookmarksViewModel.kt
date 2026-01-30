package com.gorman.feature.bookmarks.impl.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.data.repository.mapevents.IMapEventsRepository
import com.gorman.data.repository.user.IUserRepository
import com.gorman.feature.auth.api.SignInScreenNavKey
import com.gorman.feature.bookmarks.impl.states.BookmarksScreenState
import com.gorman.feature.bookmarks.impl.states.BookmarksScreenUiEvent
import com.gorman.feature.details.api.DetailsScreenNavKey
import com.gorman.feature.events.api.HomeScreenNavKey
import com.gorman.navigation.navigator.Navigator
import com.gorman.ui.mappers.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val userRepository: IUserRepository,
    private val navigator: Navigator,
    private val mapEventsRepository: IMapEventsRepository
) : ViewModel() {
    private val retryTrigger = MutableSharedFlow<Unit>(replay = 1).apply {
        tryEmit(Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<BookmarksScreenState> = retryTrigger
        .flatMapLatest {
            combine(
                userRepository.getUserFavouriteEvents(),
                userRepository.getUserData(),
                ::Pair
            )
        }
        .flatMapLatest { (ids, user) ->
            if (ids.isEmpty()) {
                flowOf(BookmarksScreenState.Success(persistentListOf(), user.toUiState()))
            } else {
                val flowEvents = ids.map { id ->
                    mapEventsRepository.getEventById(id).map { it.toUiState() }
                }
                combine(flowEvents) { events ->
                    BookmarksScreenState.Success(
                        bookmarks = events.toPersistentList(),
                        userUiState = user.toUiState()
                    ) as BookmarksScreenState
                }
            }
        }
        .catch { e ->
            emit(BookmarksScreenState.Error(e))
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = BookmarksScreenState.Loading,
            started = SharingStarted.WhileSubscribed(5000L)
        )

    fun onUiEvent(event: BookmarksScreenUiEvent) {
        when (event) {
            BookmarksScreenUiEvent.OnRetryClick -> retryTrigger.tryEmit(Unit)
            is BookmarksScreenUiEvent.OnEventClick -> navigator.goTo(DetailsScreenNavKey(event.eventId))
            is BookmarksScreenUiEvent.ChangeLikeState -> {
                viewModelScope.launch {
                    userRepository.updateFavouriteEventsState(event.eventId)
                }
            }
            BookmarksScreenUiEvent.OnExploreClick -> navigator.setRoot(HomeScreenNavKey)
            BookmarksScreenUiEvent.OnSignOutClick -> {
                viewModelScope.launch {
                    navigator.setRoot(SignInScreenNavKey)
                    userRepository.signOut()
                }
            }
        }
    }
}
