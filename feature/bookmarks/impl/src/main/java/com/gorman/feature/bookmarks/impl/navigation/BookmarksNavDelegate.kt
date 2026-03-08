package com.gorman.feature.bookmarks.impl.navigation

import com.gorman.feature.auth.api.SignInScreenNavKey
import com.gorman.feature.details.api.DetailsScreenNavKey
import com.gorman.feature.events.api.HomeScreenNavKey
import com.gorman.navigation.Navigator

class BookmarksNavDelegate(val navigator: Navigator) {
    fun onDetailsClick(id: String) { navigator.navigate(DetailsScreenNavKey(id)) }
    fun onSignInClick() { navigator.navigate(SignInScreenNavKey) }
    fun onSignOutClick() { navigator.navigate(SignInScreenNavKey) }
    fun onExploreClick() { navigator.navigate(HomeScreenNavKey) }
}
