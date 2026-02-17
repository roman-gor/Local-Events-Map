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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.gorman.feature.bookmarks.api.BookmarksScreenNavKey
import com.gorman.feature.details.api.DetailsScreenNavKey
import com.gorman.feature.events.api.HomeScreenNavKey
import com.gorman.localeventsmap.navigation.LocalEventsMapNavigation
import com.gorman.localeventsmap.states.MainUiSideEffects
import com.gorman.localeventsmap.ui.bottombar.BottomNavigationBar
import com.gorman.localeventsmap.viewmodels.MainViewModel
import com.gorman.navigation.navigator.Navigator
import com.gorman.ui.theme.LocalEventsMapTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var entryBuilders: Set<@JvmSuppressWildcards EntryProviderScope<NavKey>.() -> Unit>

    val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.onDeepLinkReceived(intent.data.toString())
        enableEdgeToEdge()
        setContent {
            LocalEventsMapTheme {
                val context = LocalContext.current
                val resources = LocalResources.current
                val currentKey = navigator.backStack.lastOrNull()

                SideEffectsListener(
                    sideEffect = mainViewModel.sideEffect,
                    intent = intent,
                    onNavigate = { navigator.goTo(DetailsScreenNavKey(it)) },
                    showErrorToast = { Toast.makeText(context, resources.getString(it), Toast.LENGTH_SHORT).show() }
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        AnimatedVisibility(
                            visible = showBottomBar(currentKey),
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

    fun showBottomBar(currentKey: NavKey?): Boolean {
        return currentKey is HomeScreenNavKey || currentKey is BookmarksScreenNavKey
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        mainViewModel.onDeepLinkReceived(intent.data.toString())
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
