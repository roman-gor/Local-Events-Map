package com.gorman.events.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gorman.events.ui.states.DataStatus

@SuppressLint("ComposeModifierMissing")
@Composable
fun StatusBanner(status: DataStatus) {
    if (status == DataStatus.FRESH) return

    val (text, color) = when (status) {
        DataStatus.OFFLINE -> "Нет сети. Показаны сохраненные данные" to MaterialTheme.colorScheme.errorContainer
        DataStatus.OUTDATED -> "Данные могли устареть. Обновите список" to MaterialTheme.colorScheme.tertiaryContainer
        else -> "" to Color.Transparent
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
