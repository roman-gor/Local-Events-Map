package com.gorman.feature.auth.impl.navigation

import com.gorman.feature.auth.api.SignInScreenNavKey
import com.gorman.feature.auth.api.SignUpScreenNavKey
import com.gorman.feature.events.api.HomeScreenNavKey
import com.gorman.navigation.navigator.Navigator

class AuthNavDelegate(val navigator: Navigator) {
    fun onSignIn() { navigator.navigateTo(SignInScreenNavKey) }
    fun onSignUp() { navigator.navigateTo(SignUpScreenNavKey) }
    fun setHomeRoot() { navigator.setRoot(HomeScreenNavKey) }
}
