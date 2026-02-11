package com.gorman.localeventsmap

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.gorman.data.repository.mapevents.IMapEventsRepository
import com.gorman.feature.bookmarks.api.BookmarksScreenNavKey
import com.gorman.feature.details.api.DetailsScreenNavKey
import com.gorman.feature.events.api.HomeScreenNavKey
import com.gorman.feature.setup.api.SetupScreenNavKey
import com.gorman.localeventsmap.navigation.LocalEventsMapNavigation
import com.gorman.localeventsmap.ui.bottombar.BottomNavigationBar
import com.gorman.navigation.navigator.IAppNavigator
import com.gorman.navigation.navigator.NavIntent
import com.gorman.navigation.state.rememberNavigationState
import com.gorman.navigation.state.toEntries
import com.gorman.ui.theme.LocalEventsMapTheme
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: IAppNavigator

    @Inject
    lateinit var entryBuilders: Set<@JvmSuppressWildcards EntryProviderScope<NavKey>.() -> Unit>

    @Inject
    lateinit var mapEventsRepository: IMapEventsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleDeepLink(intent)
        enableEdgeToEdge()
        setContent {
            MapKitFactory.getInstance().onStart()
            LocalEventsMapTheme {
                val navState = rememberNavigationState(startRoute = SetupScreenNavKey)
                LaunchedEffect(navigator.navigationEvents) {
                    navigator.navigationEvents.collect { intent ->
                        when (intent) {
                            is NavIntent.NavigateTo -> navState.navigateTo(intent.key)
                            is NavIntent.SwitchTab -> {
                                if (intent.key == navState.currentTab) {
                                    navState.popToRoot()
                                } else {
                                    navState.navigateTo(intent.key)
                                }
                            }
                            is NavIntent.GoBack -> { if (!navState.goBack()) finish() }
                            is NavIntent.PopToRoot -> navState.popToRoot()
                            is NavIntent.SetRoot -> navState.setRoot(intent.key)
                        }
                    }
                }

                val showBottomBar = navState.currentVisibleKey is HomeScreenNavKey ||
                        navState.currentVisibleKey is BookmarksScreenNavKey
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        AnimatedVisibility(
                            visible = showBottomBar,
                            enter = slideInVertically { it },
                            exit = slideOutVertically { it }
                        ) {
                            BottomNavigationBar(
                                currentKey = navState.currentTab,
                                onNavigateTo = { key -> navigator.switchTab(key) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .pointerInput(Unit) { detectTapGestures(onTap = {}, onPress = {}) }
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
                        onBack = { navigator.goBack() },
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
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        if (intent?.action != Intent.ACTION_VIEW) return

        val uri = intent.data ?: return
        if (uri.scheme == "app" && uri.host == "events") {
            val eventId = uri.lastPathSegment
            if (!eventId.isNullOrBlank()) {
                lifecycleScope.launch {
                    val result = mapEventsRepository.syncEventById(eventId).onFailure { e ->
                        Log.e("Sync Event By Id", "Failed saving event: ${e.message}")
                    }
                    if (result.isSuccess) {
                        navigator.navigateTo(DetailsScreenNavKey(eventId))
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.eventNotFound),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                setIntent(null)
            }
        }
    }
}
