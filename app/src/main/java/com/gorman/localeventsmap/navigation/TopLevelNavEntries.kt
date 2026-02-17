package com.gorman.localeventsmap.navigation

import androidx.navigation3.runtime.NavKey
import com.gorman.feature.bookmarks.api.BookmarksScreenNavKey
import com.gorman.feature.events.api.HomeScreenNavKey
import com.gorman.localeventsmap.R

enum class TopLevelNavEntries(val title: Int, val icon: Int, val route: NavKey) {
    HomeEntry(
        title = R.string.homeItemTitle,
        icon = R.drawable.ic_home,
        route = HomeScreenNavKey
    ),
    BookmarkEntry(
        title = R.string.bookmarksItemTitle,
        icon = R.drawable.ic_bookmark,
        route = BookmarksScreenNavKey
    )
}
