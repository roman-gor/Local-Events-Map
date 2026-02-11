package com.gorman.feature.details.impl.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.data.repository.bookmarks.IBookmarksRepository
import com.gorman.data.repository.mapevents.IMapEventsRepository
import com.gorman.data.repository.user.IUserRepository
import com.gorman.domainmodel.BookmarkData
import com.gorman.feature.details.api.DetailsScreenNavKey
import com.gorman.feature.details.impl.ui.states.DetailsScreenState
import com.gorman.feature.details.impl.ui.states.DetailsScreenUiEvent
import com.gorman.navigation.navigator.IAppNavigator
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okio.IOException

@HiltViewModel(assistedFactory = DetailsViewModel.Factory::class)
class DetailsViewModel @AssistedInject constructor(
    mapEventsRepository: IMapEventsRepository,
    private val bookmarksRepository: IBookmarksRepository,
    private val userRepository: IUserRepository,
    private val navigator: IAppNavigator,
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
            userRepository.getUserData()
        }
        .flatMapLatest { user ->
            val eventFlow = mapEventsRepository.getEventById(_id)
            if (user == null) {
                eventFlow.map { event ->
                    DetailsScreenState.Success(
                        uid = null,
                        event = event.toUiState().copy(isFavourite = false)
                    ) as DetailsScreenState
                }
            } else {
                val bookmarksFlow = bookmarksRepository.getBookmarkedEvents(user.uid)
                combine(eventFlow, bookmarksFlow) { event, bookmarkedEvents ->
                    val isFav = bookmarkedEvents.any { it.id == event.id }
                    DetailsScreenState.Success(
                        uid = user.uid,
                        event.toUiState().copy(isFavourite = isFav)
                    ) as DetailsScreenState
                }
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
            DetailsScreenUiEvent.OnNavigateToBack -> navigator.goBack()
            DetailsScreenUiEvent.OnRetryClick -> retryTrigger.tryEmit(Unit)
        }
    }

    private fun onFavouriteChange(id: String) {
        viewModelScope.launch {
            (uiState.value as DetailsScreenState.Success).uid?.let {
                bookmarksRepository.updateBookmark(it, BookmarkData(id)).onFailure { e ->
                    Log.e("Details VM", "Error updating state of favourite: ${e.message}")
                }
            }
        }
    }
}
