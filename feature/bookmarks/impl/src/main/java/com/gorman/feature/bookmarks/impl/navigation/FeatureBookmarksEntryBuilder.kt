package com.gorman.feature.bookmarks.impl.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.gorman.feature.bookmarks.api.BookmarksScreenNavKey
import com.gorman.feature.bookmarks.impl.ui.screens.BookmarksScreenEntry
import com.gorman.feature.bookmarks.impl.ui.viewmodels.BookmarksViewModel
import com.gorman.navigation.navigator.LocalNavigator
import com.gorman.ui.theme.LocalEventsMapTheme

fun EntryProviderScope<NavKey>.featureBookmarksEntryBuilder() {
    entry<BookmarksScreenNavKey> {
        val navigator = LocalNavigator.current
        val viewModel = hiltViewModel<BookmarksViewModel, BookmarksViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(BookmarksNavDelegate(navigator))
            }
        )
        BookmarksScreenEntry(
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .systemBarsPadding()
                .padding(top = LocalEventsMapTheme.dimens.paddingLarge),
            bookmarksViewModel = viewModel
        )
    }
}
