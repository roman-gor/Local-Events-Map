package com.gorman.feature.setup.impl.navigation

import com.gorman.feature.auth.api.SignInScreenNavKey
import com.gorman.feature.events.api.HomeScreenNavKey
import com.gorman.navigation.navigator.Navigator

class SetupNavDelegate(val navigator: Navigator) {
    fun setHomeRoot() { navigator.setRoot(HomeScreenNavKey) }
    fun setSignInRoot() { navigator.setRoot(SignInScreenNavKey) }
}
