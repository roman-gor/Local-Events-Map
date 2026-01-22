package com.gorman.localeventsmap

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.gorman.detailsevent.screens.DetailsEventScreenEntry
import com.gorman.ui.states.MapUiEvent
import com.gorman.ui.theme.LocalEventsMapTheme
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MapKitFactory.getInstance().onStart()
            LocalEventsMapTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                ) {
                    DetailsEventScreenEntry(
                        mapUiEvent = MapUiEvent(
                            name = "Открытие библиотеки",
                            description = "Будет проходить открытие библиотеки: экскурсия и призы" +
                                " Будет проходить открытие библиотеки: экскурсия и призы" +
                                " Будет проходить открытие библиотеки: экскурсия и призы" +
                                " Будет проходить открытие библиотеки: экскурсия и призы" +
                                " Будет проходить открытие библиотеки: экскурсия и призы" +
                                " Будет проходить открытие библиотеки: экскурсия и призы" +
                                " Будет проходить открытие библиотеки: экскурсия и призы" +
                                " Будет проходить открытие библиотеки: экскурсия и призы" +
                                " Будет проходить открытие библиотеки: экскурсия и призы",
                            date = 1769241600000L,
                            address = "пр. Независимости, 62",
                            cityName = "minsk",
                            category = "education",
                            coordinates = "53.6822, 23.8330",
                            isSelected = true,
                            isFavourite = false,
                            id = "event2"
                        ),
                        modifier = Modifier.fillMaxSize().systemBarsPadding()
                    )
                }
            }
        }
    }
}
