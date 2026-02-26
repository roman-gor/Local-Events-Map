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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.gorman.feature.details.api.DetailsScreenNavKey
import com.gorman.feature.setup.api.SetupScreenNavKey
import com.gorman.localeventsmap.navigation.LocalEventsMapNavigation
import com.gorman.localeventsmap.states.MainUiState
import com.gorman.localeventsmap.ui.bottombar.BottomNavigationBar
import com.gorman.localeventsmap.ui.bottombar.shouldShowBottomBar
import com.gorman.localeventsmap.viewmodels.MainViewModel
import com.gorman.navigation.navigator.LocalNavigator
import com.gorman.navigation.navigator.Navigator
import com.gorman.navigation.state.rememberNavigationState
import com.gorman.navigation.state.toEntries
import com.gorman.ui.theme.LocalEventsMapTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var entryBuilders: Set<@JvmSuppressWildcards EntryProviderScope<NavKey>.() -> Unit>

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel.onDeepLinkReceived(intent.dataString)

        enableEdgeToEdge()
        setContent {
            LocalEventsMapTheme {
                val navState = rememberNavigationState(startRoute = SetupScreenNavKey)
                val navigator = remember(navState) { Navigator(navState) }

                HandleEffects(
                    stateFlow = mainViewModel.mainUiState,
                    onNavigateToEvent = {
                        navigator.navigateTo(DetailsScreenNavKey(it))
                        intent.data = null
                    },
                    showErrorToast = {
                        Toast.makeText(this@MainActivity, getString(it), Toast.LENGTH_SHORT).show()
                    }
                )

                CompositionLocalProvider(LocalNavigator provides navigator) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            AnimatedVisibility(
                                visible = shouldShowBottomBar(navState.currentVisibleKey),
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
                        val currentEntries = navState.toEntries(combinedEntryProvider).toPersistentList()
                        LocalEventsMapNavigation(
                            entries = currentEntries,
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
        mainViewModel.onDeepLinkReceived(intent.dataString)
    }
}


@Composable
private fun HandleEffects(
    stateFlow: Flow<MainUiState>,
    onNavigateToEvent: (String) -> Unit,
    showErrorToast: (Int) -> Unit
) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    LaunchedEffect(stateFlow, lifecycleOwner) {
        stateFlow.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { state ->
                when (state) {
                    is MainUiState.NavigateToEvent -> { onNavigateToEvent(state.eventId) }
                    is MainUiState.ShowToast -> { showErrorToast(state.res) }
                }
            }
    }
}

