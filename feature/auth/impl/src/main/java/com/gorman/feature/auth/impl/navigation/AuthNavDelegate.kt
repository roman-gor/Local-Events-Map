package com.gorman.feature.auth.impl.navigation

import com.gorman.feature.auth.api.SignInScreenNavKey
import com.gorman.feature.auth.api.SignUpScreenNavKey
import com.gorman.feature.events.api.HomeScreenNavKey
import com.gorman.navigation.Navigator

class AuthNavDelegate(val navigator: Navigator) {
    fun onSignIn() { navigator.navigate(SignInScreenNavKey) }
    fun onSignUp() { navigator.navigate(SignUpScreenNavKey) }
    fun setHomeRoot() { navigator.navigate(HomeScreenNavKey) }
}
