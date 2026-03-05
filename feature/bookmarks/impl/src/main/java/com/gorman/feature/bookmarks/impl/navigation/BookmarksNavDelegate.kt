package com.gorman.feature.bookmarks.impl.navigation

import com.gorman.feature.auth.api.SignInScreenNavKey
import com.gorman.feature.details.api.DetailsScreenNavKey
import com.gorman.feature.events.api.HomeScreenNavKey
import com.gorman.navigation.navigator.Navigator

class BookmarksNavDelegate(val navigator: Navigator) {
    fun onDetailsClick(id: String) { navigator.navigateTo(DetailsScreenNavKey(id)) }
    fun onSignInClick() { navigator.navigateTo(SignInScreenNavKey) }
    fun onSignOutClick() { navigator.setRoot(SignInScreenNavKey) }
    fun onExploreClick() { navigator.setRoot(HomeScreenNavKey) }
}
