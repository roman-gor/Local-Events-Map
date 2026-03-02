package com.gorman.feature.bookmarks.impl.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gorman.feature.bookmarks.impl.R
import com.gorman.feature.bookmarks.impl.ui.states.BookmarksScreenState
import com.gorman.feature.bookmarks.impl.ui.states.BookmarksScreenUiEvent
import com.gorman.feature.bookmarks.impl.ui.viewmodels.BookmarksViewModel
import com.gorman.ui.components.ErrorDataScreen
import com.gorman.ui.components.LoadingStub
import com.gorman.ui.theme.LocalEventsMapTheme

@Composable
fun BookmarksScreenEntry(
    modifier: Modifier = Modifier,
    bookmarksViewModel: BookmarksViewModel = hiltViewModel()
) {
    val uiState by bookmarksViewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is BookmarksScreenState.Error -> ErrorDataScreen(
            text = stringResource(com.gorman.ui.R.string.errorDataLoading),
            onRetryClick = { bookmarksViewModel.onUiEvent(BookmarksScreenUiEvent.OnRetryClick) }
        )
        BookmarksScreenState.Loading -> LoadingStub()
        is BookmarksScreenState.Success -> BookmarksScreen(
            uiState = state,
            onUiEvent = bookmarksViewModel::onUiEvent,
            modifier = modifier
        )
    }
}

@Composable
fun BookmarksScreen(
    uiState: BookmarksScreenState.Success,
    onUiEvent: (BookmarksScreenUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(R.string.account),
            style = MaterialTheme.typography.titleLarge,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LocalEventsMapTheme.dimens.paddingExtraLarge)
        )
        Spacer(modifier = Modifier.height(8.dp))
        UserDataCard(
            user = uiState.userUiState,
            onSignOutClick = { onUiEvent(BookmarksScreenUiEvent.OnSignOutClick) },
            onSignUpClick = { onUiEvent(BookmarksScreenUiEvent.OnSignInClick) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LocalEventsMapTheme.dimens.paddingExtraLarge)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.favoriteEvents),
            style = MaterialTheme.typography.titleLarge,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LocalEventsMapTheme.dimens.paddingExtraLarge)
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (uiState.bookmarks.isNotEmpty()) {
            BookmarkList(
                events = uiState.bookmarks,
                onEventClick = { onUiEvent(BookmarksScreenUiEvent.OnEventClick(it)) },
                onLikeButtonClick = { onUiEvent(BookmarksScreenUiEvent.ChangeLikeState(it)) },
                modifier = Modifier.padding(horizontal = LocalEventsMapTheme.dimens.paddingLarge)
            )
        } else {
            EmptyListPlaceholder(
                onExploreClick = { onUiEvent(BookmarksScreenUiEvent.OnExploreClick) },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun EmptyListPlaceholder(
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.emptyLikes),
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 16.sp
        )
        TextButton(
            onClick = { onExploreClick() }
        ) {
            Text(
                text = stringResource(R.string.explore),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}
