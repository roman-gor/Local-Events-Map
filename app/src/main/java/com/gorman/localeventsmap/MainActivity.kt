package com.gorman.localeventsmap

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.gorman.data.repository.mapevents.IMapEventsRepository
import com.gorman.feature.bookmarks.api.BookmarksScreenNavKey
import com.gorman.feature.details.api.DetailsScreenNavKey
import com.gorman.feature.events.api.HomeScreenNavKey
import com.gorman.localeventsmap.navigation.LocalEventsMapNavigation
import com.gorman.localeventsmap.ui.bottombar.BottomNavigationBar
import com.gorman.navigation.navigator.Navigator
import com.gorman.ui.theme.LocalEventsMapTheme
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var entryBuilders: Set<@JvmSuppressWildcards EntryProviderScope<NavKey>.() -> Unit>

    @Inject
    lateinit var mapEventsRepository: IMapEventsRepository

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleDeepLink(intent)
        enableEdgeToEdge()
        setContent {
            MapKitFactory.getInstance().onStart()
            LocalEventsMapTheme {
                val currentKey = navigator.backStack.lastOrNull()

                val showBottomBar = currentKey is HomeScreenNavKey || currentKey is BookmarksScreenNavKey
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        AnimatedVisibility(
                            visible = showBottomBar,
                            enter = slideInVertically { it },
                            exit = slideOutVertically { it }
                        ) {
                            BottomNavigationBar(
                                currentKey = currentKey,
                                onNavigateTo = { key -> navigator.setRoot(key) },
                                modifier = Modifier.fillMaxWidth()
                                    .padding(LocalEventsMapTheme.dimens.paddingLarge)
                                    .clip(CircleShape)
                            )
                        }
                    },
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                ) {
                    LocalEventsMapNavigation(
                        navigator = navigator,
                        entryBuilders = entryBuilders,
                        modifier = Modifier.fillMaxSize()
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
                    val result = mapEventsRepository.syncEventById(eventId)
                    if (result.isSuccess) {
                        navigator.goTo(DetailsScreenNavKey(eventId))
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
