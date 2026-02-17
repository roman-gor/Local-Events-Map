package com.gorman.localeventsmap

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.gorman.feature.bookmarks.api.BookmarksScreenNavKey
import com.gorman.feature.details.api.DetailsScreenNavKey
import com.gorman.feature.events.api.HomeScreenNavKey
import com.gorman.feature.setup.api.SetupScreenNavKey
import com.gorman.localeventsmap.navigation.LocalEventsMapNavigation
import com.gorman.localeventsmap.states.MainUiSideEffects
import com.gorman.localeventsmap.ui.bottombar.BottomNavigationBar
import com.gorman.localeventsmap.viewmodels.MainViewModel
import com.gorman.localeventsmap.viewmodel.MainViewModel
import com.gorman.navigation.navigator.LocalNavigator
import com.gorman.navigation.navigator.Navigator
import com.gorman.navigation.state.rememberNavigationState
import com.gorman.navigation.state.toEntries
import com.gorman.ui.theme.LocalEventsMapTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var entryBuilders: Set<@JvmSuppressWildcards EntryProviderScope<NavKey>.() -> Unit>

    val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.onDeepLinkReceived(intent.data.toString())
        enableEdgeToEdge()
        setContent {
            LocalEventsMapTheme {
                val navState = rememberNavigationState(startRoute = SetupScreenNavKey)
                val navigator = Navigator(navState)
                val context = LocalContext.current
                val resources = LocalResources.current

                SideEffectsListener(
                    sideEffect = mainViewModel.deepLinkSideEffect,
                    intent = intent,
                    onNavigateToDetails = { navigator.navigateTo(DetailsScreenNavKey(it)) },
                    showErrorToast = { Toast.makeText(context, resources.getString(it), Toast.LENGTH_SHORT).show() }
                )

                CompositionLocalProvider(LocalNavigator provides navigator) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            AnimatedVisibility(
                                visible = showBottomBar(navState.currentVisibleKey),
                                enter = slideInVertically { it },
                                exit = slideOutVertically { it }
                            ) {
                                BottomNavigationBar(
                                    currentKey = navState.currentTab,
                                    onNavigateTo = { key -> navigator.switchTab(key) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            horizontal = LocalEventsMapTheme.dimens.paddingExtraExtraLarge,
                                            vertical = LocalEventsMapTheme.dimens.paddingLarge
                                        )
                                        .systemBarsPadding()
                                        .clip(CircleShape)
                                )
                            }
                        },
                        contentWindowInsets = WindowInsets.safeDrawing.only(
                            sides = WindowInsetsSides.Horizontal
                        ),
                    ) { paddingValues ->
                        val combinedEntryProvider = entryProvider {
                            entryBuilders.forEach { builder -> this.builder() }
                        }
                        val currentEntries = navState.toEntries(combinedEntryProvider)
                        LocalEventsMapNavigation(
                            entries = currentEntries.toPersistentList(),
                            onBack = { if (!navigator.goBack()) finish() },
                            modifier = Modifier
                                .fillMaxSize()
                                .consumeWindowInsets(paddingValues)
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        mainViewModel.onDeepLinkReceived(intent.data.toString())
    }

    private fun showBottomBar(currentKey: NavKey?): Boolean {
        return currentKey is HomeScreenNavKey || currentKey is BookmarksScreenNavKey
    }
}

@Composable
private fun SideEffectsListener(
    sideEffect: Flow<MainUiSideEffects>,
    onNavigate: (String) -> Unit,
    showErrorToast: (Int) -> Unit,
    intent: Intent
) {
    LaunchedEffect(sideEffect) {
        sideEffect.collect { effect ->
            when (effect) {
                is MainUiSideEffects.NavigateToEvent -> {
                    onNavigate(effect.eventId)
                    intent.data = null
                }
                is MainUiSideEffects.ShowToast -> { showErrorToast(effect.res) }
            }
        }
    }
}
