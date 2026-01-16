package com.gorman.localeventsmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.gorman.events.ui.screens.MapScreenEntry
import com.gorman.ui.theme.LocalEventsMapTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocalEventsMapTheme {
                Surface(
                    modifier = Modifier.systemBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MapScreenEntry()
                }
            }
        }
    }
}
