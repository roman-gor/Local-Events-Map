package com.gorman.localeventsmap.navigation

import androidx.navigation3.runtime.NavKey
import com.gorman.feature.details.api.DetailsScreenNavKey
import com.gorman.feature.events.api.HomeScreenNavKey
import com.gorman.localeventsmap.R

sealed class NavEntries(val title: Int, val icon: Int, val route: NavKey) {
    object HomeItem : NavEntries(
        title = R.string.homeItemTitle,
        icon = R.drawable.ic_home,
        route = HomeScreenNavKey
    )
    object BookmarkItem : NavEntries(
        title = R.string.bookmarksItemTitle,
        icon = R.drawable.ic_bookmark,
        route = DetailsScreenNavKey("event2")
    )

    companion object {
        val entries = listOf(
            HomeItem,
            BookmarkItem
        )
    }
}
