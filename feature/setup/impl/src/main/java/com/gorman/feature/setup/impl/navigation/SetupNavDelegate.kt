package com.gorman.feature.setup.impl.navigation

import com.gorman.feature.auth.api.SignInScreenNavKey
import com.gorman.feature.events.api.HomeScreenNavKey
import com.gorman.navigation.Navigator

class SetupNavDelegate(val navigator: Navigator) {
    fun setHomeRoot() { navigator.navigate(HomeScreenNavKey) }
    fun setSignInRoot() { navigator.navigate(SignInScreenNavKey) }
}
