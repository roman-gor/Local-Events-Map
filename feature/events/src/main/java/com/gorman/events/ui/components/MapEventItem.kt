package com.gorman.events.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gorman.events.ui.states.MapUiEvent
import com.gorman.ui.theme.LocalEventsMapTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@SuppressLint("ComposeModifierMissing")
@Composable
fun MapEventItem(
    mapEvent: MapUiEvent,
    onEventClick: (MapUiEvent) -> Unit
) {
    val date = mapEvent.date?.let { convertMillisToDate(it) }
    val containerColor = if (mapEvent.isSelected) { MaterialTheme.colorScheme.primary } else {
        MaterialTheme.colorScheme.background
    }
    val textColor = if (mapEvent.isSelected) { MaterialTheme.colorScheme.inverseOnSurface } else {
        MaterialTheme.colorScheme.onSurface
    }
    Row(
        modifier = Modifier.fillMaxWidth().background(
            color = containerColor
        )
            .clickable(onClick = { onEventClick(mapEvent) }),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.wrapContentWidth()
                .padding(LocalEventsMapTheme.dimens.paddingLarge),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            mapEvent.name?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            mapEvent.address?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = textColor
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = date ?: "",
            style = MaterialTheme.typography.bodyLarge,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            color = textColor,
            modifier = Modifier
                .padding(LocalEventsMapTheme.dimens.paddingLarge)
        )
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = DateTimeFormatter.ofPattern("d MMMM")
    val instant = Instant.ofEpochMilli(millis)
    val date = instant.atZone(ZoneId.systemDefault()).toLocalDate()
    return date.format(formatter)
}
