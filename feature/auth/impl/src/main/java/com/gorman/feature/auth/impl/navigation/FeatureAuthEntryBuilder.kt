package com.gorman.feature.auth.impl.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.gorman.feature.auth.api.SignInScreenNavKey
import com.gorman.feature.auth.api.SignUpScreenNavKey
import com.gorman.feature.auth.impl.ui.screens.SignInScreenEntry
import com.gorman.feature.auth.impl.ui.screens.SignUpScreenEntry
import com.gorman.feature.auth.impl.ui.viewmodels.AuthViewModel
import com.gorman.navigation.navigator.LocalNavigator
import com.gorman.ui.theme.LocalEventsMapTheme

fun EntryProviderScope<NavKey>.featureAuthEntryBuilder() {
    entry<SignUpScreenNavKey> {
        val navigator = LocalNavigator.current
        val viewModel = hiltViewModel<AuthViewModel, AuthViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(AuthNavDelegate(navigator))
            }
        )
        SignUpScreenEntry(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(LocalEventsMapTheme.dimens.paddingLarge),
            authViewModel = viewModel
        )
    }
    entry<SignInScreenNavKey> {
        val navigator = LocalNavigator.current
        val viewModel = hiltViewModel<AuthViewModel, AuthViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(AuthNavDelegate(navigator))
            }
        )
        SignInScreenEntry(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(LocalEventsMapTheme.dimens.paddingLarge),
            authViewModel = viewModel
        )
    }
}
