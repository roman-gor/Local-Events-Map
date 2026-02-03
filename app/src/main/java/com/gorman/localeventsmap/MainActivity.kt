package com.gorman.localeventsmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.gorman.feature.events.impl.ui.screens.mapscreen.MapScreenEntry
import com.gorman.localeventsmap.ui.theme.LocalEventsMapTheme
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MapKitFactory.getInstance().onStart()
            LocalEventsMapTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                ) { innerPaddings ->
                    MapScreenEntry(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPaddings)
                            .background(color = MaterialTheme.colorScheme.background)
                    )
                }
            }
        }
    }
}
