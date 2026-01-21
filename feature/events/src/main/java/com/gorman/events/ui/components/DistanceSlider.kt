package com.gorman.events.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gorman.ui.theme.LocalEventsMapTheme

@SuppressLint("ComposeModifierMissing")
@Composable
fun DistanceSlider(
    distance: Int,
    enabled: Boolean,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = LocalEventsMapTheme.dimens.paddingExtraLarge)
            .navigationBarsPadding()
    ) {
        Text(
            text = "$distance км",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Slider(
            value = distance.toFloat(),
            onValueChange = {
                onValueChange(it)
            },
            onValueChangeFinished = {
                onValueChangeFinished()
            },
            enabled = enabled,
            valueRange = 1f..20f,
            steps = 20
        )
    }
}
