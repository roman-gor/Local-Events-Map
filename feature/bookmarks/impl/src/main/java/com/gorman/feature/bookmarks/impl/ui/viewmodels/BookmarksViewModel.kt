package com.gorman.feature.bookmarks.impl.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.data.repository.bookmarks.IBookmarksRepository
import com.gorman.data.repository.user.IUserRepository
import com.gorman.domainmodel.BookmarkData
import com.gorman.feature.auth.api.SignInScreenNavKey
import com.gorman.feature.bookmarks.impl.domain.SignOutUserUseCase
import com.gorman.feature.bookmarks.impl.ui.states.BookmarksScreenState
import com.gorman.feature.bookmarks.impl.ui.states.BookmarksScreenUiEvent
import com.gorman.feature.details.api.DetailsScreenNavKey
import com.gorman.feature.events.api.HomeScreenNavKey
import com.gorman.navigation.navigator.Navigator
import com.gorman.ui.mappers.toUiState
import com.gorman.ui.states.UserUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val userRepository: IUserRepository,
    private val bookmarksRepository: IBookmarksRepository,
    private val signOutUserUseCase: SignOutUserUseCase,
    private val navigator: Navigator
) : ViewModel() {
    private val retryTrigger = MutableSharedFlow<Unit>(replay = 1).apply {
        tryEmit(Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<BookmarksScreenState> = retryTrigger
        .flatMapLatest {
            userRepository.getUserData()
        }
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(BookmarksScreenState.Success(persistentListOf(), UserUiState()))
            } else {
                bookmarksRepository.getBookmarkedEvents(user.uid)
                    .flatMapLatest { bookmarks ->
                        flowOf(
                            BookmarksScreenState.Success(
                                bookmarks = bookmarks.map { it.toUiState() }.toPersistentList(),
                                userUiState = user.toUiState()
                            ) as BookmarksScreenState
                        )
                    }
            }
        }.catch { e ->
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
                    val uid = (uiState.value as BookmarksScreenState.Success).userUiState.uid
                    bookmarksRepository
                        .updateBookmark(uid, BookmarkData(event.eventId))
                        .onFailure { e ->
                            Log.e("Bookmark Toggle", "Error toggle in remote: ${e.message}")
                        }
                }
            }
            BookmarksScreenUiEvent.OnExploreClick -> navigator.setRoot(HomeScreenNavKey)
            BookmarksScreenUiEvent.OnSignOutClick -> {
                viewModelScope.launch {
                    navigator.setRoot(SignInScreenNavKey)
                    signOutUserUseCase()
                }
            }
            BookmarksScreenUiEvent.OnSignInClick -> {
                viewModelScope.launch {
                    navigator.goTo(SignInScreenNavKey)
                }
            }
        }
    }
}
