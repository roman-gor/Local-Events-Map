package com.gorman.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.gorman.ui.R
import com.gorman.ui.theme.LocalEventsMapTheme

@SuppressLint("ComposeModifierMissing")
@Composable
fun ErrorDataScreen(
    text: String,
    onRetryClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(LocalEventsMapTheme.dimens.paddingLarge)
            )
            Button(
                onClick = { onRetryClick() }
            ) {
                Text(
                    text = stringResource(R.string.tryAgain),
                    fontSize = 16.sp
                )
            }
        }
    }
}
