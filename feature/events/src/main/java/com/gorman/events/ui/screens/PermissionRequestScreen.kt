package com.gorman.events.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.gorman.ui.theme.LocalEventsMapTheme

@Composable
fun PermissionRequestScreen(
    shouldShowRationale: Boolean,
    requestPermissions: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(LocalEventsMapTheme.dimens.paddingExtraLarge),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(LocalEventsMapTheme.dimens.paddingLarge)
        ) {
            val text = if (shouldShowRationale) {
                "Для отображения событий на карте и вашего местоположения необходим доступ к геолокации. Пожалуйста, предоставьте разрешение."
            } else {
                "Для работы приложения требуется доступ к вашему местоположению."
            }
            Text(
                text = text,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Button(onClick = requestPermissions) {
                Text("Предоставить разрешение")
            }
        }
    }
}
