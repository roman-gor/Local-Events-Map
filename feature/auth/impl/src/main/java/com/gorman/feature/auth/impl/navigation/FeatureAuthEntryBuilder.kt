package com.gorman.feature.auth.impl.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.gorman.feature.auth.api.SignInScreenNavKey
import com.gorman.feature.auth.api.SignUpScreenNavKey
import com.gorman.feature.auth.impl.ui.screens.SignInScreenEntry
import com.gorman.feature.auth.impl.ui.screens.SignUpScreenEntry
import com.gorman.ui.theme.LocalEventsMapTheme

fun EntryProviderScope<NavKey>.featureAuthEntryBuilder() {
    entry<SignUpScreenNavKey> {
        SignUpScreenEntry(
            modifier = Modifier.fillMaxSize().padding(LocalEventsMapTheme.dimens.paddingLarge)
        )
    }
    entry<SignInScreenNavKey> {
        SignInScreenEntry(
            modifier = Modifier.fillMaxSize().padding(LocalEventsMapTheme.dimens.paddingLarge)
        )
    }
}
