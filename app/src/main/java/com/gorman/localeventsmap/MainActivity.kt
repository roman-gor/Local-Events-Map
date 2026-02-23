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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.gorman.feature.details.api.DetailsScreenNavKey
import com.gorman.localeventsmap.navigation.LocalEventsMapNavigation
import com.gorman.localeventsmap.states.MainUiState
import com.gorman.localeventsmap.ui.bottombar.BottomNavigationBar
import com.gorman.localeventsmap.ui.bottombar.shouldShowBottomBar
import com.gorman.localeventsmap.viewmodels.MainViewModel
import com.gorman.navigation.navigator.Navigator
import com.gorman.ui.theme.LocalEventsMapTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var entryBuilders: Set<@JvmSuppressWildcards EntryProviderScope<NavKey>.() -> Unit>

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel.onDeepLinkReceived(intent.dataString)

        mainViewModel.mainUiState
            .flowWithLifecycle(lifecycle)
            .onEach(::handleEffects)
            .launchIn(lifecycleScope)

        enableEdgeToEdge()
        setContent {
            LocalEventsMapTheme {
                val currentKey = navigator.backStack.lastOrNull()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        AnimatedVisibility(
                            visible = shouldShowBottomBar(currentKey),
                            enter = slideInVertically { it },
                            exit = slideOutVertically { it }
                        ) {
                            BottomNavigationBar(
                                currentKey = currentKey,
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
                    LocalEventsMapNavigation(
                        navigator = navigator,
                        entryBuilders = entryBuilders,
                        modifier = Modifier
                            .fillMaxSize()
                            .consumeWindowInsets(paddingValues)
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        mainViewModel.onDeepLinkReceived(intent.dataString)
    }

    private fun handleEffects(state: MainUiState) {
        when (state) {
            is MainUiState.NavigateToEvent -> {
                navigator.goTo(DetailsScreenNavKey(state.eventId))
                intent.data = null
            }
            is MainUiState.ShowToast -> {
                Toast.makeText(this, getString(state.res), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
